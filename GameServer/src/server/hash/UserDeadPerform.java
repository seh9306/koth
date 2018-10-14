package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import hash.key.ChangeMapKey;
import hash.key.ChangeMapSuccessKey;
import hash.key.LogoutKey;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import model.GameMap;
import model.Monster;
import model.User;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.CharacterAction;
import value.MapValue;
import value.ProtocolValue;

public class UserDeadPerform implements SignalPerform {
	private Server server;
	private ReadLock readLock;
	private Vector<ClientSocketThread> clientSocketThreadVec;
	private HashMap<Integer, GameMap> gameMapHashMap;

	public UserDeadPerform(Server server) {
		this.server = server;
		readLock = server.getLockForSocketArr().readLock();
		gameMapHashMap = server.getMapHashMap();
		clientSocketThreadVec = server.getThreadVector();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		User user = clientSocketThread.getUser();
		ChangeMapKey changeMapKey = (ChangeMapKey) key;
		int oldMapIndex = changeMapKey.getOldMapIndex();
		int action = CharacterAction.DOWN;
		String id = changeMapKey.getId();
		GameMap gm = gameMapHashMap.get(oldMapIndex);
		Vector<UserPosInfo> userPosInfoVec = gm.getUserPosInfoVec();
		UserPosInfo userPosInfo = null;
		synchronized (userPosInfoVec) {
			int size = userPosInfoVec.size();
			for (int i = 0; i < size; i++) {
				userPosInfo = userPosInfoVec.get(i);
				if (userPosInfo.getId().equals(id))
					break;
			}
		}
		userPosInfoVec.remove(userPosInfo);

		server.removeMapHashMapData(userPosInfo, oldMapIndex);
		LogoutKey logoutKey = new LogoutKey(id);
		logoutKey.setProtocol(ProtocolValue.LOGOUT);
		readLock.lock();
		int size = clientSocketThreadVec.size();
		for (int i = 0; i < size; i++) {
			ClientSocketThread cst = clientSocketThreadVec.get(i);
			if (cst.getGameid() != null && cst.getMapIndex() == oldMapIndex && !oos.equals(cst.getOos())) {
				try {
					cst.getOos().writeObject(logoutKey);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		readLock.unlock(); // ÇØ´ç ¸Ê¿¡¼­ ·Î±×¾Æ¿ô °³³äÀ¸·Î ³»º¸³¿

		int posX = 0;
		int posY = 0;

		posX = 6 * 60;
		posY = 7 * 60;
		
		userPosInfo = new UserPosInfo(id, posX, posY, 0, action, null, user.isArmor(), user.isWeapon());
		GameMap gameMap = gameMapHashMap.get(1);
		gameMap.setMapValue(posX / 60, posY / 60, MapValue.USER); // À¯Àú°¡ ÇØ´ç ÁÂÇ¥¿¡
																	// ÂïÈù´Ù.
		UserActionKey userActionKey = new UserActionKey();
		userActionKey.setProtocol(ProtocolValue.USER_APPEAR);
		userActionKey.setId(id);
		userActionKey.setPosX(posX);
		userActionKey.setPosY(posY);
		userActionKey.setMovingY(action);
		userActionKey.setArmor(user.isArmor());
		userActionKey.setWeapon(user.isWeapon());
		System.out.println(userActionKey.getPosX() + "/¾îÇÇ¾î Å°4/"+ userActionKey.getPosY());
		
		synchronized (gameMap.getUserPosInfoVec()) {
			gameMap.getUserPosInfoVec().add(userPosInfo);
		}

		// Àü¼Û
		
		readLock.lock();
		int size2 = clientSocketThreadVec.size();
		for (int i = 0; i < size2; i++) {

			ClientSocketThread otherClientSocketThread = clientSocketThreadVec.get(i);

			ObjectOutputStream oos2 = otherClientSocketThread.getOos();
			if (otherClientSocketThread.getGameid() != null && !oos.equals(oos2)
					&& otherClientSocketThread.getMapIndex() == 1) {
				System.out.println("¸ÊÀÌµ¿ ½ÃÀü");
				synchronized (oos2) {
					try {
						oos2.writeObject(userActionKey);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (oos.equals(oos2)) {
				System.out.println("¸ÊÀÌµ¿ ¿Ï·á");
				otherClientSocketThread.setUserPosInfo(userPosInfo);
				otherClientSocketThread.setMapIndex(1);
			}
		}
		readLock.unlock();

		ChangeMapSuccessKey changeMapSuccessKey = new ChangeMapSuccessKey();
		changeMapSuccessKey.setProtocol(ProtocolValue.CHANGE_MAP_SUCCESS);
		changeMapSuccessKey.setMapIndex(1);
		synchronized (gameMap.getUserPosInfoVec()) {
			changeMapSuccessKey.setUserPosInfoVec(new Vector<UserPosInfo>(gameMap.getUserPosInfoVec()));

			synchronized (gameMap.getMonsterVector()) {
				//changeMapSuccessKey.setMonsterVec(gameMap.getMonsterVector());
				Vector<Monster> newMonsterVec = new Vector<Monster>();
				for (int i = 0; i < gameMap.getMonsterVector().size(); i++) {
					newMonsterVec.add(new Monster(gameMap.getMonsterVector().get(i)));
					/*System.out.println(gameMap.getMonsterVector().get(i).getId()+"//"+gameMap.getMonsterVector().get(i).getPosX() + "///d///"
							+ gameMap.getMonsterVector().get(i).getPosY());*/
				}
				changeMapSuccessKey.setMonsterVec(newMonsterVec);

				try {
					oos.writeObject(changeMapSuccessKey);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}

	}
}