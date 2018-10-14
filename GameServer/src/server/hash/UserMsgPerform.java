package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import hash.key.SignalKey;
import hash.key.UserMsgKey;
import server.hash.SignalPerform;
import server.main.Server;
import server.thread.ClientSocketThread;

public class UserMsgPerform implements SignalPerform {

	private Vector<ClientSocketThread> clientSocketThreadVec;
	private ReadLock readLock;

	public UserMsgPerform(Server server) {
		clientSocketThreadVec = server.getThreadVector();
		readLock = server.getLockForSocketArr().readLock();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		
		UserMsgKey userMsgKey = (UserMsgKey) key;
		String id = userMsgKey.getId();
		String msg = userMsgKey.getMsg();
		int mapIndex = userMsgKey.getMapIndex();
		int posX = userMsgKey.getPosX();
		int posY = userMsgKey.getPosY();

		ClientSocketThread otherClientSocketThread;

		userMsgKey = new UserMsgKey(userMsgKey);
		
		readLock.lock();
		int size = clientSocketThreadVec.size();
		for (int j = 0; j < size; j++) {
			otherClientSocketThread = clientSocketThreadVec.get(j);
			String gameId = otherClientSocketThread.getGameid();
			if (gameId != null && !gameId.equals(id)) {
				if (mapIndex == otherClientSocketThread.getMapIndex()) {
					
					ObjectOutputStream oos2 = otherClientSocketThread.getOos();
					if (!oos2.equals(oos)) {
						synchronized (oos2) {
							try {
								oos2.writeObject(userMsgKey);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		readLock.unlock();
	}

}
