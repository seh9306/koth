package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.LoginKey;
import hash.key.LoginSuccessKey;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import model.GameMap;
import model.Item;
import model.User;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.MapValue;
import value.ProtocolValue;

public class LoginPerform implements SignalPerform {
	//private Server server;
	private GameDAO gameDAO;
	private ResultSet rs;
	private GameMap userMap;
	private HashMap<Integer, GameMap> mapHashMap;
	private ReentrantReadWriteLock lock;
	private Vector<ClientSocketThread> clientSocketThreadVec;
	
	public LoginPerform(Server server){
		//this.server = server;
		gameDAO = server.getGameDAO();
		mapHashMap = server.getMapHashMap();
		lock = server.getLockForSocketArr();
		clientSocketThreadVec = server.getThreadVector();
	}
	
	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		LoginKey loginKey = (LoginKey)key;
		
		String id = loginKey.getId();
		String pw = loginKey.getPw();
		String query = "select u.user_posx, u.user_posy,"
				+ "u.user_movingy , u.user_level, u.user_armor, u.user_weapon ,"
				+ "u.user_hp, u.user_mp, u.user_class, u.user_exp, u.user_gold, u.user_on,"
				+ "m.map_index "
				+ "from user_info u, map_info m "
				+ "where user_id='"+id+"' "
				+ "and user_pw='"+pw+"' "
				+ "and u.user_map_index = m.map_index ";
		try {
			rs = gameDAO.returnResultAfterQuery(query);
			
			if(rs.next()){
				if(rs.getInt("user_on")==1)
					return;
				String onQuery = "update user_info set user_on = 1 where user_id = '"+id+"'";
				gameDAO.updateQuery(onQuery);
				//생성
				int mapIndex = rs.getInt("map_index");
				int posX = rs.getInt("user_posx");
				int posY = rs.getInt("user_posy");
				int movingY = rs.getInt("user_movingy");
				int level = rs.getInt("user_level");
				int hp = rs.getInt("user_hp");
				int mp = rs.getInt("user_mp");
				String job = rs.getString("user_class");
				int exp = rs.getInt("user_exp");
				int gold = rs.getInt("user_gold");
				int armor = rs.getInt("user_armor");
				int weapon = rs.getInt("user_weapon");

				String itemQuery = "select"
						+ " i.item_name, i.item_effect, i.item_kind,"
						+ " o.own_position, o.user_id"
						+ " from own_info o, item_info i"
						+ " where o.item_index = i.item_index and "
						+ " o.user_id = '" + id+"'";
				rs = gameDAO.returnResultAfterQuery(itemQuery);
				
				Vector<Item> itemVec = new Vector<Item>();
				System.out.println("아이템을 벡터에 삽입합니다.");
				while(rs.next()){
					Item item = new Item();
					item.setName(rs.getString("item_name"));
					item.setEffect(rs.getInt("item_effect"));
					item.setKind(rs.getInt("item_kind"));
					item.setPosition(rs.getString("own_position").charAt(0));
					
					System.out.println(item.getName());
					
					itemVec.add(item);
				}
				
				User user = new User();
				user.setId(id);
				user.setJob(job);
				user.setMapIndex(mapIndex);
				user.setPosX(posX);
				user.setPosY(posY);
				user.setMovingY(movingY);
				user.setLevel(level);
				user.setHp(hp);
				user.setMp(mp);
				user.setExp(exp);
				user.setGold(gold);
				user.setItemVec(itemVec);
				if(armor==0){
					user.setArmor(false);
				}else{
					user.setArmor(true);
				}
				if(weapon==0){
					user.setWeapon(false);
				}else{
					user.setWeapon(true);
				}
				
				clientSocketThread.setUser(user);
				
				UserPosInfo userPosInfo = new UserPosInfo(id,
						posX,
						posY,
						0,
						movingY, null, user.isArmor(),user.isWeapon());
				
				userMap = mapHashMap.get(mapIndex);
				userMap.setMapValue(posX/60, posY/60, MapValue.USER); // 유저가 해당 좌표에 찍힌다.
				synchronized(userMap.getUserPosInfoVec()){
					userMap.getUserPosInfoVec().add(userPosInfo); // 접속 종료시에는 따로 전달할 것이기 때문에 동기화 하지 않는다.
				}
				System.out.println(userMap.getMapValue(posX/60, posY/60)+"맵값");
				LoginSuccessKey loginSuccessCharacterInfoKey = new LoginSuccessKey();
				
				loginSuccessCharacterInfoKey.setProtocol(ProtocolValue.LOGIN_SUCCESS);
				loginSuccessCharacterInfoKey.setId(id);
				loginSuccessCharacterInfoKey.setUserPosInfoVec(userMap.getUserPosInfoVec());
				loginSuccessCharacterInfoKey.setMonsterVec(userMap.getMonsterVector());
				loginSuccessCharacterInfoKey.setMapIndex(mapIndex);
				loginSuccessCharacterInfoKey.setLevel(level);
				loginSuccessCharacterInfoKey.setHp(hp);
				loginSuccessCharacterInfoKey.setMp(mp);
				loginSuccessCharacterInfoKey.setJob(job);
				loginSuccessCharacterInfoKey.setExp(exp);
				loginSuccessCharacterInfoKey.setGold(gold);
				loginSuccessCharacterInfoKey.setItemVec(itemVec);
				
				//loginSuccessCharacterInfoKey.setUserPosInfo(userPosInfo);
				
				//전송
				synchronized(oos){
					oos.writeObject((Object)loginSuccessCharacterInfoKey);
				}
				
				//생성
				UserActionKey userMovingKey = new UserActionKey();
				userMovingKey.setProtocol(ProtocolValue.USER_APPEAR);
				userMovingKey.setId(id);
				userMovingKey.setPosX(posX);
				userMovingKey.setPosY(posY);
				userMovingKey.setMovingY(movingY);
				userMovingKey.setArmor(user.isArmor());
				userMovingKey.setWeapon(user.isWeapon());
				
				//전송
				lock.readLock().lock();
				int size = clientSocketThreadVec.size();
				for(int i = 0; i < size; i++ ){
					
					ClientSocketThread otherClientSocketThread = clientSocketThreadVec.get(i);
					
					ObjectOutputStream oos2 = otherClientSocketThread.getOos();
					if(otherClientSocketThread.getGameid()!=null&&!oos.equals(oos2)&&otherClientSocketThread.getMapIndex()==mapIndex){
						System.out.println("로그인시전");
						synchronized(oos2){
							oos2.writeObject(userMovingKey);
						}
					}else if(oos.equals(oos2)){
						System.out.println("아이디 삽입 완료");
						otherClientSocketThread.setGameid(id);
						otherClientSocketThread.setUserPosInfo(userPosInfo);
						otherClientSocketThread.setMapIndex(mapIndex);
					}
				}
				lock.readLock().unlock();
				return;
			}
			
			System.out.println("로그인 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
