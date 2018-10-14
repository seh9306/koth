package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.SignalKey;
import hash.key.TakeOffKey;
import hash.key.UseItemKey;
import model.Item;
import model.User;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.hash.SignalPerform;
import server.main.Server;
import server.thread.ClientSocketThread;
import value.ProtocolValue;

public class TakeOffPerform implements SignalPerform {

	private Vector<ClientSocketThread> clientSocketThreadVec;
	private ReentrantReadWriteLock lock;

	public TakeOffPerform(Server server) {
		clientSocketThreadVec = server.getThreadVector();
		lock = server.getLockForSocketArr();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		
		TakeOffKey takeOffKey = (TakeOffKey) key;
		Item item = takeOffKey.getItem();
		User user = clientSocketThread.getUser();
		UserPosInfo userPosInfo = clientSocketThread.getUserPosInfo();
		Vector<Item> itemVec = user.getItemVec();
		System.out.println("옷을 벗었습니다.");
		int size = itemVec.size();
		System.out.println("size = "+size);
		
		char position = 'a';
		for (int i = 0; i < size; i++) {
			Item otherItem = itemVec.get(i);
			if(otherItem.getName()=="")
			System.out.println("문자를 출력합니다...."+otherItem.getPosition());
			while(otherItem.getPosition()==position){
				position++;
			}
		}
		
		item.setPosition(position);
		String query = "INSERT INTO `koth`.`own_info` (`item_index`, `own_position`, `user_id`) ";
		itemVec.add(item);
		if(item.getKind() == 0){
			user.setWeapon(false);
			userPosInfo.setWeapon(false);
			query += "VALUES ('3', '"+position+"', '"+user.getId()+"');"; 
		}else if(item.getKind() == 1){
			user.setArmor(false);
			userPosInfo.setArmor(false);
			query += "VALUES ('2', '"+position+"', '"+user.getId()+"');";
		}
		
		GameDAO.getInstance().updateQuery(query);
		
		TakeOffKey takeOffKeyForSend = new TakeOffKey();
		takeOffKeyForSend.setProtocol(ProtocolValue.TAKE_OFF);
		takeOffKeyForSend.setItem(item);
		synchronized(oos){
			try {
				oos.writeObject(takeOffKeyForSend);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		TakeOffKey takeOffKeyForSend2 = new TakeOffKey();
		Item itemForSend = new Item();
		itemForSend.setKind(item.getKind());
		itemForSend.setName(user.getId());
		itemForSend.setPosition('.');
		
		takeOffKeyForSend2.setProtocol(ProtocolValue.TAKE_OFF);
		takeOffKeyForSend2.setItem(itemForSend);
		
		lock.readLock().lock();
		int size2 = clientSocketThreadVec.size();
		ClientSocketThread otherClientSocketThread;
		for (int j = 0; j < size2; j++) {
			otherClientSocketThread = clientSocketThreadVec.get(j);
			String id = otherClientSocketThread.getGameid();
			if (id != null) {
				if(id != user.getId())
				if (!clientSocketThread.equals(otherClientSocketThread)&&otherClientSocketThread.getMapIndex()==user.getMapIndex()) {
					ObjectOutputStream oos2 = otherClientSocketThread.getOos();
					try {
						synchronized(oos2){
							oos2.writeObject(takeOffKeyForSend2);
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
