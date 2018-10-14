package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import hash.key.AndroidAddFriendKey;
import hash.key.AndroidDeleteFriendKey;
import hash.key.AndroidLoginSuccessKey;
import hash.key.SignalKey;
import model.UserInfo;
import server.dao.GameDAO;
import server.hash.SignalPerform;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.ProtocolValue;

public class AndroidDeleteFriendPerform implements SignalPerform {
	private GameDAO gameDAO;
	private ResultSet rs;
	
	public AndroidDeleteFriendPerform(Server server) {
		gameDAO = server.getGameDAO();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		AndroidDeleteFriendKey androidDeleteFriendKey = (AndroidDeleteFriendKey) key;
		
		String myID = androidDeleteFriendKey.getMyID();
		String friendID = androidDeleteFriendKey.getFriendID();
		String updateQuery = "DELETE FROM `koth`.`friend_info` WHERE `user_id`= '"+myID+"' "+"and `friend_id`= '"+friendID+"'";
				
		gameDAO.updateQuery(updateQuery);
		
		String query = "select "
				+ "user_level,"
				+ "user_hp, user_mp "
				+ "from user_info "
				+ "where user_id='"+myID+"' ";
		
		rs = gameDAO.returnResultAfterQuery(query);
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		try {
			if(rs.next()){
				int level = rs.getInt("user_level");
				int hp = rs.getInt("user_hp");
				int mp = rs.getInt("user_mp");
				
				query = "select u.user_id, u.user_hp, u.user_mp, u.user_level "
						+ "from user_info u, friend_info f "
						+ "where f.user_id = '"+myID+"' "
								+ "and f.friend_id = u.user_id";
				rs = gameDAO.returnResultAfterQuery(query);
				while(rs.next()){
					UserInfo tmp = new UserInfo();
					tmp.setName(rs.getString("user_id"));
					tmp.setHp(rs.getInt("user_hp"));
					tmp.setMp(rs.getInt("user_mp"));
					tmp.setLevel(rs.getInt("user_level"));
					userInfoList.add(tmp);
				}
				
				AndroidLoginSuccessKey androidLoginSuccessKey = new AndroidLoginSuccessKey();
				androidLoginSuccessKey.setProtocol(ProtocolValue.ANDROID_LOGIN_SUCCESS);
				
				UserInfo userInfo = new UserInfo();
				userInfo.setHp(hp);
				userInfo.setItems(null);
				userInfo.setLevel(level);
				userInfo.setMp(mp);
				userInfo.setName(myID);
				
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
