package server.hash;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hash.key.MonsterDeadKey;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import hash.key.UserMovingYKey;
import model.GameMap;
import model.Item;
import model.Monster;
import model.User;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.main.Server;
import server.thread.ClientSocketThread;
import server.thread.MonsterGenThread;
import value.CharacterAction;
import value.ProtocolValue;

public class UserMovingPerform implements SignalPerform {

	private HashMap<Integer, GameMap> mapHashMap;
	private Vector<ClientSocketThread> clientSocketThreadVec;
	private ReentrantReadWriteLock lock;
	private HashMap<Integer, MonsterGenThread> monsterGenThreadHashMap;

	public UserMovingPerform(Server server) {
		mapHashMap = server.getMapHashMap();
		clientSocketThreadVec = server.getThreadVector();
		lock = server.getLockForSocketArr();
		monsterGenThreadHashMap = server.getMonsterGenThreadHashMap();
	}

	@Override
	public void performAction(ClientSocketThread clientSocketThread, SignalKey key) {
		ObjectOutputStream oos = clientSocketThread.getOos();
		ClientSocketThread otherClientSocketThread;
		UserActionKey userActionKey = (UserActionKey) key;
		UserPosInfo oldUserPosInfo = clientSocketThread.getUserPosInfo();
		User user = clientSocketThread.getUser();
		GameMap gameMap = mapHashMap.get(userActionKey.getMapIndex());
		Monster monsterMap[][] = gameMap.getMonsterMap();
		Vector<UserPosInfo> userPosInfoVec = gameMap.getUserPosInfoVec();
		int i, vecSize, mapIndex = clientSocketThread.getMapIndex();
		String id = clientSocketThread.getGameid();

		int posX = userActionKey.getPosX() / 60;
		int posY = userActionKey.getPosY() / 60;
		int action = userActionKey.getAction();
		if (action > 3) { // 공격 모션이다!
			// 공격 처리하고
			Monster monster = null;
			synchronized (monsterMap) {
				switch (action) {
				case CharacterAction.UP_SPACE:
					if(posY != 0)
						monster = monsterMap[posY - 1][posX];
					break;
				case CharacterAction.DOWN_SPACE:
					if(posY != gameMap.getHeight()-1)
						monster = monsterMap[posY + 1][posX];
					break;
				case CharacterAction.RIGHT_SPACE:
					if(posX != gameMap.getWidth()-1)
						monster = monsterMap[posY][posX + 1];
					break;
				case CharacterAction.LEFT_SPACE:
					if(posX != 0)
						monster = monsterMap[posY][posX - 1];
					break;
				}

			}
			if (monster != null) {
				monster.setAggroId(id);
				System.out.print("타격전 : " + monster.getHp());
				monster.setHp(monster.getHp() - 50);
				System.out.println(" 타격 후 : " + monster.getHp());
				if (monster.getHp() <= 0) {
					MonsterDeadKey monsterDeadKey = new MonsterDeadKey();
					monsterDeadKey.setProtocol(ProtocolValue.MONSTER_DEAD);
					monsterDeadKey.setId(monster.getId());
					monsterDeadKey.setExp(monster.getExp());
					Item item = new Item();
					item.setName("도토리");
					item.setKind(2);
					item.setEffect(500);
					
					Vector<Item> itemVec = user.getItemVec();
					System.out.println("옷을 벗었습니다.");
					int size = itemVec.size();
					
					char position = 'a';
					for (int i2 = 0; i2 < size; i2++) {
						Item otherItem = itemVec.get(i2);
						while(otherItem.getPosition()==position){
							position++;
						}
					}
					
					item.setPosition(position);
					itemVec.add(item);
					monsterDeadKey.setItem(item);
					String query = "INSERT INTO `koth`.`own_info` (`item_index`, `own_position`, `user_id`) "
							+ "VALUES ('5', '"+position+"', '"+user.getId()+"');";
					GameDAO.getInstance().updateQuery(query);
					
					int totalExp = user.getExp() + monster.getExp();
					int level = user.getLevel();
					
					while(totalExp >= level * 100 ){
						totalExp -= level * 100;
						level++;
					}
					user.setExp(totalExp);
					user.setLevel(level);
					synchronized (oos) {
						try {
							oos.writeObject(monsterDeadKey);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					monsterGenThreadHashMap.get(userActionKey.getMapIndex()).getMonsterActionThreadHashMap()
							.get(monster.getId()).interrupt();
				}

			}
			// 전송

		} else {

			if (gameMap.getMapValue(posX, posY) < 50) { // 비충돌시
				int oldX = oldUserPosInfo.getX() / 60;
				int oldY = oldUserPosInfo.getY() / 60;
				userActionKey.setOldX(oldX * 60);
				userActionKey.setOldY(oldY * 60);
				gameMap.setMapValue(posX, posY, 51);
				gameMap.setMapValue(oldX, oldY, 0);

				if (posX > oldX) {
					oldUserPosInfo.setMovingY(CharacterAction.RIGHT);
				} else if (posX < oldX) {
					oldUserPosInfo.setMovingY(CharacterAction.LEFT);
				} else {
					if (posY > oldY) {
						oldUserPosInfo.setMovingY(CharacterAction.DOWN);
					} else if (posY < oldY) {
						oldUserPosInfo.setMovingY(CharacterAction.UP);
					}
				}
				oldUserPosInfo.setX(posX * 60);
				oldUserPosInfo.setY(posY * 60);
				user.setPosX(posX * 60);
				user.setPosY(posY * 60);
				user.setMovingY(oldUserPosInfo.getMovingY());
			} else {
				userActionKey.setProtocol(ProtocolValue.USER_BLOCK);
				userActionKey.setPosX(oldUserPosInfo.getX());
				userActionKey.setPosY(oldUserPosInfo.getY());
				synchronized (oos) {
					try {
						oos.writeObject(userActionKey);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return;
			}

		}

		lock.readLock().lock();
		int size = clientSocketThreadVec.size();
		for (int j = 0; j < size; j++) {
			otherClientSocketThread = clientSocketThreadVec.get(j);
			if (otherClientSocketThread.getGameid() != null) {
				if (mapIndex == otherClientSocketThread.getMapIndex()) {
					ObjectOutputStream oos2 = otherClientSocketThread.getOos();
					synchronized (oos2) {
						try {
							oos2.writeObject(userActionKey);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		lock.readLock().unlock();

	}

}
