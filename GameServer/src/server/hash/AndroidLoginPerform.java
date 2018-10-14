package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.AndroidLoginKey;
import hash.key.AndroidLoginSuccessKey;
import hash.key.SignalKey;
import model.Item;
import model.UserInfo;
import server.dao.GameDAO;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.ProtocolValue;

public class AndroidLoginPerform implements SignalPerform {
	private GameDAO gameDAO;
	private ResultSet rs;
	
	public AndroidLoginPerform(Server server) {
		gameDAO = server.getGameDAO();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		
		AndroidLoginKey androidLoginKey = (AndroidLoginKey) key;
		
		String id = androidLoginKey.getId();
		String pw = androidLoginKey.getPw();
		String query = "select "
				+ "user_level,"
				+ "user_hp, user_mp "
				+ "from user_info "
				+ "where user_id='"+id+"' "
				+ "and user_pw='"+pw+"' ";
		
		rs = gameDAO.returnResultAfterQuery(query);
		
		try {
			if(rs.next()){
				//clientSocketThread.setGameid(id);
				int level = rs.getInt("user_level");
				int hp = rs.getInt("user_hp");
				int mp = rs.getInt("user_mp");
				
				query = "select u.user_id, u.user_hp, u.user_mp, u.user_level "
						+ "from user_info u, friend_info f "
						+ "where f.user_id = '"+id+"' "
								+ "and f.friend_id = u.user_id";
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				rs = gameDAO.returnResultAfterQuery(query);
				while(rs.next()){
					UserInfo tmp = new UserInfo();
					tmp.setName(rs.getString("user_id"));
					tmp.setHp(rs.getInt("user_hp"));
					tmp.setMp(rs.getInt("user_mp"));
					tmp.setLevel(rs.getInt("user_level"));
					userInfoList.add(tmp);
				}
				query = "select * from item_info";
				ArrayList<Item> itemList = new ArrayList<Item>();
				rs = gameDAO.returnResultAfterQuery(query);
				
				while(rs.next()){
					Item item = new Item();
					item.setName(rs.getString("item_name"));
					item.setKind(rs.getInt("item_kind"));
					item.setEffect(rs.getInt("item_effect"));
					item.setExpain(rs.getString("item_explain"));
					itemList.add(item);
				}
				
				AndroidLoginSuccessKey androidLoginSuccessKey = new AndroidLoginSuccessKey();
				androidLoginSuccessKey.setProtocol(ProtocolValue.ANDROID_LOGIN_SUCCESS);
				
				UserInfo userInfo = new UserInfo();
				userInfo.setHp(hp);
				userInfo.setItems(null);
				userInfo.setLevel(level);
				userInfo.setMp(mp);
				userInfo.setName(id);
				
				androidLoginSuccessKey.setItemList(itemList);
				androidLoginSuccessKey.setFriendUserInfoList(userInfoList);
				androidLoginSuccessKey.setUserInfo(userInfo);
				try {
					synchronized(oos){
						oos.writeObject(androidLoginSuccessKey);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
