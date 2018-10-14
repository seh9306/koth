package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.SignalKey;
import hash.key.UserMovingYKey;
import model.User;
import server.main.Server;
import server.thread.ClientSocketThread;

public class UserMovingYPerform implements SignalPerform {

	private Vector<ClientSocketThread> clientSocketThreadVec;
	private ReentrantReadWriteLock lock;

	public UserMovingYPerform(Server server) {
		clientSocketThreadVec = server.getThreadVector();
		lock = server.getLockForSocketArr();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		
		UserMovingYKey userMovingYKey = (UserMovingYKey) key;
		User user = clientSocketThread.getUser();
		
		user.setMovingY(userMovingYKey.getMovingY());
		
		lock.readLock().lock();
		int size = clientSocketThreadVec.size();
		ClientSocketThread otherClientSocketThread;
		for (int j = 0; j < size; j++) {
			otherClientSocketThread = clientSocketThreadVec.get(j);
			String id = otherClientSocketThread.getGameid();
			if (id != null) {
				if (!id.equals(userMovingYKey.getId())&&otherClientSocketThread.getMapIndex()==userMovingYKey.getMapIndex()) {
					ObjectOutputStream oos2 = otherClientSocketThread.getOos();
					try {
						synchronized(oos2){
							oos2.writeObject(userMovingYKey);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		lock.readLock().unlock();
	}

}
