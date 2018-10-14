package server.thread;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import hash.key.MonsterAttackKey;
import hash.key.MonsterDeadKey;
import hash.key.MonsterMovingKey;
import hash.key.MonsterMovingYKey;
import model.GameMap;
import model.Monster;
import model.UserPosInfo;
import server.main.Server;
import value.CharacterAction;
import value.ProtocolValue;

public class MonsterActionThread extends Thread {
	private Server server;
	private Monster monster;
	private GameMap gameMap;
	private ReadLock readLock;
	// private WriteLock writeLock;
	private int mapIndex;
	private Vector<ClientSocketThread> threadVector;
	private Vector<UserPosInfo> userPosInfoVec;
	private Monster monsterMap[][];
	private HashMap<Integer, MonsterActionThread> monsterActionThreadHashMap;

	public MonsterActionThread(Server server) {
		this.server = server;
		threadVector = server.getThreadVector();
		readLock = server.getLockForSocketArr().readLock();
		gameMap = server.getMapHashMap().get(mapIndex);
		// writeLock = server.getLockForSocketArr().writeLock();
		monsterMap = gameMap.getMonsterMap();
		userPosInfoVec = gameMap.getUserPosInfoVec();
	}

	public void changeMovingY() {
		MonsterMovingYKey monsterMovingYKey = new MonsterMovingYKey(ProtocolValue.MONSTER_MOVINGY, monster.getId(),
				monster.getMovingY());
		readLock.lock();
		int size = threadVector.size();
		for (int j = 0; j < size; j++) {
			ClientSocketThread thread = threadVector.get(j);
			if (mapIndex == thread.getMapIndex() && thread.getGameid() != null) {
				try {
					synchronized (thread.getOos()) {
						thread.getOos().writeObject(monsterMovingYKey);
					}
					// thread.getOos().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		readLock.unlock();
	}

	public void attackUser() {
		MonsterAttackKey monsterAttackKey = new MonsterAttackKey();
		monsterAttackKey.setProtocol(ProtocolValue.MONSTER_ATTACK);
		monsterAttackKey.setDmg(monster.getDmg());

		readLock.lock();
		int size = threadVector.size();
		for (int j = 0; j < size; j++) {
			ClientSocketThread thread = threadVector.get(j);
			if (mapIndex == thread.getMapIndex()) {
				if (thread.getGameid().equals(monster.getAggroId())) {
					thread.getUser().setHp(thread.getUser().getHp()-monster.getDmg());
					try {
						synchronized (thread.getOos()) {
							
							thread.getOos().writeObject(monsterAttackKey);
						}
						// thread.getOos().flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		readLock.unlock();
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {

		while (true) {
			try {
				sleep(((int) Math.random() * 500) + 1000);

				int moving = (int) (Math.random() * 4);
				int posX = monster.getPosX() / 60;
				int posY = monster.getPosY() / 60;

				if (monster.getAggroId() == null) {//
					switch (moving) {
					case 0:
						// System.out.println("왼쪽으로간다.");
						if (posX != 0)
							posX -= 1;
						break;
					case 1:
						// System.out.println("위로간다");
						if (posY != 0)
							posY -= 1;
						break;
					case 2:
						// System.out.println("오른쪽으로 간다");
						if (posX != gameMap.getWidth() - 1)
							posX += 1;
						break;
					case 3:
						// System.out.println("아래로간다");
						if (posY != gameMap.getHeight() - 1)
							posY += 1;
						break;
					}
				} else {
					System.out.println("몬스터가 어그로를 먹음");
					int i, size;
					UserPosInfo userPosInfo;
					synchronized (userPosInfoVec) {
						size = userPosInfoVec.size();
						 userPosInfo = null;
						for (i = 0; i < size; i++) {
							userPosInfo = userPosInfoVec.get(i);
							if (userPosInfo.getId().equals(monster.getAggroId()))
								break;
						}
					}
					if (i == size) {
						monster.setAggroId(null);
						continue;
					}
					int userX = userPosInfo.getX() / 60;
					int userY = userPosInfo.getY() / 60;
					if (moving % 2 == 0) {
						System.out.println("userX : " + userX + ", posX : " + posX);
						if (userX - 1 > posX) { // 몹이 왼쪽에 있음
							posX += 1;
							moving = 2;
						} else if (userX + 1 < posX) { // 몹이 오른쪽에 있음
							posX -= 1;
							moving = 0;
						} else { // X좌표상 x
							if (userY - 1 > posY) {
								posY += 1;
								moving = 3;
							} else if (userY + 1 < posY) {
								posY -= 1;
								moving = 1;
							} else {
								if (userY != posY && userX != posX) {
									if (userX > posX) { // 몹이 왼쪽에 있음
										posX += 1;
										moving = 2;
									} else if (userX < posX) { // 몹이 오른쪽에 있음
										posX -= 1;
										moving = 0;
									}
								} else {
									// 공격
									int movingY = monster.getMovingY();
									if (userY - 1 == posY) {// up
										if (movingY != CharacterAction.DOWN) {
											monster.setMovingY(CharacterAction.DOWN);
											changeMovingY();
											continue;
										}
									} else if (userY + 1 == posY) { // down
										if (movingY != CharacterAction.UP) {
											monster.setMovingY(CharacterAction.UP);
											changeMovingY();
											continue;
										}
									} else if (userX + 1 == posX) { // right
										if (movingY != CharacterAction.LEFT) {
											monster.setMovingY(CharacterAction.LEFT);
											changeMovingY();
											continue;
										}
									} else if (userX - 1 == posX) { // left
										if (movingY != CharacterAction.RIGHT) {
											monster.setMovingY(CharacterAction.RIGHT);
											changeMovingY();
											continue;
										}
									}
									attackUser();
								}
							}
						}
					} else {
						if (userY - 1 > posY) { // 몹이 왼쪽에 있음
							posY += 1;
							moving = 3;
						} else if (userY + 1 < posY) { // 몹이 오른쪽에 있음
							posY -= 1;
							moving = 1;
						} else { // X좌표상 x
							if (userX - 1 > posX) {
								posX += 1;
								moving = 2;
							} else if (userX + 1 < posX) {
								posX -= 1;
								moving = 0;
							} else {
								if (userY != posY && userX != posX) {
									if (userY > posY) { // 몹이 왼쪽에 있음
										posY += 1;
										moving = 3;
									} else if (userY < posY) { // 몹이 오른쪽에 있음
										posY -= 1;
										moving = 1;
									}
								} else {
									// 공격
									int movingY = monster.getMovingY();
									if (userY - 1 == posY) {// up
										if (movingY != CharacterAction.DOWN) {
											monster.setMovingY(CharacterAction.DOWN);
											changeMovingY();
											continue;
										}
									} else if (userY + 1 == posY) { // down
										if (movingY != CharacterAction.UP) {
											monster.setMovingY(CharacterAction.UP);
											changeMovingY();
											continue;
										}
									} else if (userX + 1 == posX) { // right
										if (movingY != CharacterAction.LEFT) {
											monster.setMovingY(CharacterAction.LEFT);
											changeMovingY();
											continue;
										}
									} else if (userX - 1 == posX) { // left
										if (movingY != CharacterAction.RIGHT) {
											monster.setMovingY(CharacterAction.RIGHT);
											changeMovingY();
											continue;
										}
									}
									attackUser();
								}
							}
						}
					}
				}
				if (gameMap.getMapValue(posX, posY) < 50) {
					gameMap.setMapValue(posX, posY, 52);
					gameMap.setMapValue(monster.getPosX() / 60, monster.getPosY() / 60, 1);
					if (posX > monster.getPosX())
						monster.setMovingY(CharacterAction.RIGHT);
					else if (posX < monster.getPosX())
						monster.setMovingY(CharacterAction.LEFT);
					if (posY > monster.getPosY())
						monster.setMovingY(CharacterAction.DOWN);
					else if (posY < monster.getPosY())
						monster.setMovingY(CharacterAction.UP);
					if (posX <= -1 || posX >= gameMap.getWidth() || posY <= -1 || posY >= gameMap.getHeight()) {
						System.out.println("오류발생");
					}
					synchronized (monsterMap) {
						monsterMap[monster.getPosY() / 60][monster.getPosX() / 60] = null;
						monsterMap[posY][posX] = monster;
					}
					monster.setPosX(posX * 60);
					monster.setPosY(posY * 60);

					MonsterMovingKey monsterMovingKey = new MonsterMovingKey();
					monsterMovingKey.setProtocol(ProtocolValue.MONSTER_MOVING);
					monsterMovingKey.setAction(moving);
					Monster monster2 = new Monster(monster.getId(), monster.getMonsterName(), monster.getPosX(),
							monster.getPosY(), monster.getHp(), monster.getMovingY(), monster.getMovingX(),
							monster.getExp(), monster.getDmg());
					monsterMovingKey.setMonster(monster2);

					readLock.lock();
					int size2 = threadVector.size();
					for (int j = 0; j < size2; j++) {
						ClientSocketThread thread = threadVector.get(j);
						if (mapIndex == thread.getMapIndex() && thread.getGameid() != null) {
							try {
								synchronized (thread.getOos()) {
									thread.getOos().writeObject(monsterMovingKey);
								}
								// thread.getOos().flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					readLock.unlock();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println(monster.getHp());
				gameMap.setMapValue(monster.getPosX() / 60, monster.getPosY() / 60, 1);
				synchronized (gameMap.getMonsterMap()) {
					gameMap.getMonsterMap()[monster.getPosY() / 60][monster.getPosX() / 60] = null;
				}
				gameMap.getMonsterVector().remove(monster);
				monsterActionThreadHashMap.remove(this);
				server.getMonsterGenThreadHashMap().get(mapIndex).minusMonster();

				MonsterDeadKey monsterDeadKey = new MonsterDeadKey();
				monsterDeadKey.setProtocol(ProtocolValue.MONSTER_DEAD);
				monsterDeadKey.setId(monster.getId());
				monsterDeadKey.setExp(0);
				readLock.lock();
				int size3 = threadVector.size();
				for (int j = 0; j < size3; j++) {
					ClientSocketThread thread = threadVector.get(j);
					if (mapIndex == thread.getMapIndex() && thread.getGameid() != null) {
						try {
							synchronized (thread.getOos()) {
								thread.getOos().writeObject(monsterDeadKey);
							}
							// thread.getOos().flush();
						} catch (IOException e1) {
							e.printStackTrace();
						}
					}
				}
				readLock.unlock();
				return;
			}
		}
	}

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public HashMap<Integer, MonsterActionThread> getMonsterActionThreadHashMap() {
		return monsterActionThreadHashMap;
	}

	public void setMonsterActionThreadHashMap(HashMap<Integer, MonsterActionThread> monsterActionThreadHashMap) {
		this.monsterActionThreadHashMap = monsterActionThreadHashMap;
	}

}
