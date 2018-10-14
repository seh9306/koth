package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.SignalKey;
import hash.key.UseItemKey;
import model.Item;
import model.User;
import server.dao.GameDAO;
import server.hash.SignalPerform;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.ProtocolValue;

public class UseItemPerform implements SignalPerform {

	private Vector<ClientSocketThread> clientSocketThreadVec;
	private ReentrantReadWriteLock lock;

	public UseItemPerform(Server server) {
		clientSocketThreadVec = server.getThreadVector();
		lock = server.getLockForSocketArr();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		UseItemKey useItemKey = (UseItemKey) key;
		Item item = useItemKey.getItem();
		User user = clientSocketThread.getUser();
		Vector<Item> itemVec = user.getItemVec();
		
		int size = itemVec.size();
		
		for(int i = 0; i < size; i++){
			Item otherItem = itemVec.get(i);
			System.out.println(otherItem.getName());
			if(otherItem.getPosition() == item.getPosition()){
				itemVec.remove(otherItem);
				String query = "delete from own_info where user_id = '"+user.getId()+"' and own_position = '"+item.getPosition()+"'";
				GameDAO.getInstance().updateQuery(query);
				Item newItem = new Item();
				newItem.setKind(item.getKind());
				newItem.setName(user.getId());
				UseItemKey newUseItemKey = new UseItemKey();
				newUseItemKey.setProtocol(ProtocolValue.USE_ITEM);
				newUseItemKey.setItem(newItem);
				switch(item.getKind()){
				case 0:
					user.setWeapon(true);
					clientSocketThread.getUserPosInfo().setWeapon(true);
					break;
				case 1:
					user.setArmor(true);
					clientSocketThread.getUserPosInfo().setArmor(true);
					break;
				case 2:
					user.setHp(user.getHp()+item.getEffect());
					return;
				}
				
				lock.readLock().lock();
				int size2 = clientSocketThreadVec.size();
				ClientSocketThread otherClientSocketThread;
				for (int j = 0; j < size2; j++) {
					otherClientSocketThread = clientSocketThreadVec.get(j);
					String id = otherClientSocketThread.getGameid();
					if (id != null) {
						if (!clientSocketThread.equals(otherClientSocketThread)&&otherClientSocketThread.getMapIndex()==user.getMapIndex()) {
							ObjectOutputStream oos2 = otherClientSocketThread.getOos();
							try {
								synchronized(oos2){
									oos2.writeObject(newUseItemKey);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				lock.readLock().unlock();
				
				break;
			}
		}
		
	}

}
