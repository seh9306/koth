package server.thread;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import hash.key.MonsterZenKey;
import model.GameMap;
import model.Monster;
import model.MonsterModel;
import server.dao.GameDAO;
import server.main.Server;
import value.ProtocolValue;

public class MonsterGenThread extends Thread {
	private final static int MONSTER_NUM = 5;
	private Server server;
	private GameMap gameMap;
	private Vector<MonsterModel> monsterModelVector;
	private Vector<ClientSocketThread> threadVector;
	private HashMap<Integer, MonsterActionThread> monsterActionThreadHashMap;
	private ReadLock readLock;
	private WriteLock writeLock;
	private Monster[][] monsterMap;
	private Integer monsterNumber;

	private int mapIndex;

	public MonsterGenThread(Server server, GameMap gameMap, int mapIndex) {
		this.server = server;
		this.gameMap = gameMap;
		this.mapIndex = mapIndex;
		this.monsterActionThreadHashMap = new HashMap<Integer, MonsterActionThread>();
		this.threadVector = server.getThreadVector();
		this.readLock = server.getLockForSocketArr().readLock();
		this.writeLock = server.getLockForSocketArr().writeLock();
		this.monsterMap = gameMap.getMonsterMap();

		GameDAO gameDAO = GameDAO.getInstance();
		String query = "select m.monster_name, m.monster_hp, m.monster_exp, m.monster_dmg from monster_info m, zen_info z " + "where z.map_index = "
				+ mapIndex + " and " + "m.monster_index = z.monster_index";

		ResultSet rs = gameDAO.returnResultAfterQuery(query);
		monsterModelVector = new Vector<MonsterModel>();
		try {
			int count = 0;
			while (rs.next()) {
				count++;
				monsterModelVector.add(new MonsterModel(rs.getString("monster_name"), rs.getInt("monster_hp"), rs.getInt("monster_exp"),rs.getInt("monster_dmg")));
			}
			System.out.println(mapIndex + "맵에 " + count + "개의 몬스터 정보를 불러왔습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		monsterNumber = new Integer(0);
		MonsterActionThread monsterActionThread;
		int id = 0;

		while (true) {
			
			if (monsterNumber < MONSTER_NUM) {
				int size = monsterModelVector.size();
				for (int i = 0; i < size; i++) {
					System.out.println("몬스터를 생성합니다.");
					int posX = (int) (Math.random() * 10) * 60 + 60;
					int posY = (int) (Math.random() * 10) * 60 + 60;
					if (gameMap.getMapValue(posX / 60, posY / 60) > 50)
						continue;
					MonsterModel monsterModel = monsterModelVector.get(i);
					Monster monster = new Monster(id, monsterModel.getMonsterName(), posX, posY, monsterModel.getHp(),
							0, 0, monsterModel.getExp(),monsterModel.getDmg());

					gameMap.getMonsterVector().add(monster);
					gameMap.setMapValue(posX / 60, posY / 60, 52);
					synchronized (monsterMap) {
						monsterMap[posY / 60][posX / 60] = monster;
					}
					monsterActionThread = new MonsterActionThread(server);
					monsterActionThread.setMapIndex(mapIndex);
					monsterActionThread.setMonster(monster);
					monsterActionThread.setMonsterActionThreadHashMap(monsterActionThreadHashMap);

					monsterActionThreadHashMap.put(id++, monsterActionThread);
					readLock.lock();
					int size2 = threadVector.size();
					for (int j = 0; j < size2; j++) {
						ClientSocketThread thread = threadVector.get(j);
						if (mapIndex == thread.getMapIndex() && thread.getGameid() != null) {
							try {
								synchronized (thread.getOos()) {
									thread.getOos().writeObject(new MonsterZenKey(ProtocolValue.MONSTER_ZEN, monster));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					readLock.unlock();
					monsterActionThread.start();
					monsterNumber++;
				}
			}

			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public HashMap<Integer, MonsterActionThread> getMonsterActionThreadHashMap() {
		return monsterActionThreadHashMap;
	}

	public void minusMonster() {
		monsterNumber--;
	}

}
