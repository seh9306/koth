package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import hash.key.AndroidFriendInfoKey;
import hash.key.SearchIDKey;
import hash.key.SignalKey;
import model.UserInfo;
import server.dao.GameDAO;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.ProtocolValue;

public class AndroidSearchIDPerform implements SignalPerform{

	private GameDAO gameDAO;
	
	public AndroidSearchIDPerform(Server server) {
		gameDAO = GameDAO.getInstance();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		
		String id = ((SearchIDKey)key).getId();
		String query = "select * "
				+ "from user_info "
				+ "where user_id = '" + id + "'";
		ResultSet rs = gameDAO.returnResultAfterQuery(query);
		
		try {
			AndroidFriendInfoKey androidFriendInfoKey = new AndroidFriendInfoKey();;
			
			if(rs.next()){
				androidFriendInfoKey.setProtocol(ProtocolValue.ANDROID_EXIST_ID);
				UserInfo userInfo = new UserInfo();
				userInfo.setHp(rs.getInt("user_hp"));
				userInfo.setItems(null);
				userInfo.setLevel(rs.getInt("user_level"));
				userInfo.setMp(rs.getInt("user_mp"));
				userInfo.setName(rs.getString("user_id"));
				
				androidFriendInfoKey.setFreindInfo(userInfo);
			}else{
				androidFriendInfoKey.setProtocol(ProtocolValue.ANDROID_NOT_EXIST_ID);
			}
			
			try {
				synchronized(oos){
					oos.writeObject(androidFriendInfoKey);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
