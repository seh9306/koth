package server.main;
/*
 * Title		: Server.java
 * Create Date	: 2017 11 14 , 17:03
 * Last Modify	: 2017 11 17 , 16:27
 * 
 * Made by Seho Park 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import model.GameMap;
import model.Monster;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.hash.AndroidAddFriendPerform;
import server.hash.AndroidDeleteFriendPerform;
import server.hash.AndroidLoginPerform;
import server.hash.AndroidSearchIDPerform;
import server.hash.ChangeMapPerform;
import server.hash.LoginPerform;
import server.hash.SignalPerform;
import server.hash.TakeOffPerform;
import server.hash.UseItemPerform;
import server.hash.UserDeadPerform;
import server.hash.UserMovingPerform;
import server.hash.UserMovingYPerform;
import server.hash.UserMsgPerform;
import server.thread.ClientSocketThread;
import server.thread.MonsterActionThread;
import server.thread.MonsterGenThread;
import value.MapValue;
import value.ProtocolValue;;

public class Server {
	private final int PORT = 8888;
	
	private ServerSocket serverSocket;
	private Socket forAcceptTmpSocket;
	private GameDAO gameDAO;
	private HashMap<Integer, SignalPerform> signalPerformHashMap;
	//private HashMap<Integer, MonsterActionThread> monsterActionThreadHashMap;
	private HashMap<Integer, GameMap> mapHashMap;
	private HashMap<Integer, MonsterGenThread> monsterGenThreadHashMap;
	
	private Vector<ClientSocketThread> threadVector;
	
	private ArrayList<Socket> socketArr;
	/* 락킹이 싱크로나이즈보다 빠름
	 * 동기화를 하는 목적
	 * ex )
	 *  vc.add("1");
		vc.add("2");
		vc.add("3");
		System.out.println(vc.get(1));
		vc.remove(0);
		System.out.println(vc.get(1));
		2와 3이 출력됨
	*/
	// it is synchronized by using locking
	private ReentrantReadWriteLock forSocketReadWriteLock;
	private WriteLock socketWriteLock;
	
	private ThreadPool threadPool;
	
	public Server(){
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(PORT + "로 소켓을 생성하는데 실패하였습니다.");
		}
//		monsterActionThreadHashMap = new HashMap<Integer, MonsterActionThread>();
		threadVector = new Vector<ClientSocketThread>();
		socketArr = new ArrayList<Socket>();
		forSocketReadWriteLock = new ReentrantReadWriteLock();
		socketWriteLock = forSocketReadWriteLock.writeLock();
		threadPool = ThreadPool.getInstance(this);
		gameDAO = GameDAO.getInstance();
		
		// HashMap is not synchronized
		// Table sync
		mapHashMap = new HashMap<Integer,GameMap>();
		monsterGenThreadHashMap = new HashMap<Integer, MonsterGenThread>();
		setMap();
		
		signalPerformHashMap = new HashMap<Integer, SignalPerform>();
		signalPerformHashMap.put(ProtocolValue.LOGIN, new LoginPerform(this));
		signalPerformHashMap.put(ProtocolValue.USER_MOVING, new UserMovingPerform(this));
		signalPerformHashMap.put(ProtocolValue.USER_MOVINGY, new UserMovingYPerform(this));
		signalPerformHashMap.put(ProtocolValue.ANDROID_LOGIN, new AndroidLoginPerform(this));
		signalPerformHashMap.put(ProtocolValue.ANDROID_SEARCH_ID, new AndroidSearchIDPerform(this));
		signalPerformHashMap.put(ProtocolValue.CHANGE_MAP, new ChangeMapPerform(this));
		signalPerformHashMap.put(ProtocolValue.ANDROID_ADD_FRIEND, new AndroidAddFriendPerform(this));
		signalPerformHashMap.put(ProtocolValue.USER_MSG, new UserMsgPerform(this));
		signalPerformHashMap.put(ProtocolValue.CHARACTER_DEAD, new UserDeadPerform(this));
		signalPerformHashMap.put(ProtocolValue.USE_ITEM, new UseItemPerform(this));
		signalPerformHashMap.put(ProtocolValue.TAKE_OFF, new TakeOffPerform(this));
		signalPerformHashMap.put(ProtocolValue.ANDROID_DELETE_FRIEND, new AndroidDeleteFriendPerform(this));
	}
	
	private void setMap(){
		System.out.println("맵 정보를 불러오고 있습니다...");
		String query = "select map_index, map_id from map_info";
		ResultSet rs = gameDAO.returnResultAfterQuery(query);
		
		try {
			ObjectInputStream ois = null;
			while(rs.next()){
				String map_id = rs.getString("map_id");
				int mapIndex = rs.getInt("map_index");
				File file = new File("map//"+map_id);
				ois = new ObjectInputStream(new FileInputStream(file));
				GameMap gameMap = (GameMap)ois.readObject();
				gameMap.setMonsterVector(new Vector<Monster>());
				gameMap.setUserPosInfoVec(new Vector<UserPosInfo>());
				mapHashMap.put(mapIndex, gameMap);
				MonsterGenThread monsterGenThread = new MonsterGenThread(this,gameMap, rs.getInt("map_index"));
				monsterGenThreadHashMap.put(new Integer(mapIndex), monsterGenThread);
				monsterGenThread.start();
				System.out.println(map_id+" 맵 정보 로딩 완료");
			}
			ois.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<Socket> getSocketArr(){
		return socketArr;
	}
	
	public ReentrantReadWriteLock getLockForSocketArr(){
		return forSocketReadWriteLock;
	}
	
	public GameDAO getGameDAO(){
		return gameDAO;
	}
	
	public HashMap<Integer, SignalPerform> getSignalPerformHashMap(){
		return signalPerformHashMap;
	}
	
	public HashMap<Integer, GameMap> getMapHashMap() {
		return mapHashMap;
	}

	public void run(){ // main Thread
		try {
			while(true){ // 다중 클라이언트 접속
				System.out.println("Waiting for client connection..");
				System.out.println("Number of user : " + threadPool.getNumOfuser());
				forAcceptTmpSocket = serverSocket.accept();
				
				ClientSocketThread clientSocketThread = threadPool.getThread();
				clientSocketThread.setSocket(forAcceptTmpSocket);
				
				socketWriteLock.lock();
				socketArr.add(forAcceptTmpSocket);
				socketWriteLock.unlock();
				threadVector.add(clientSocketThread);
				String state = clientSocketThread.getState().toString();
				if(state.equals("NEW")){ // if thread is new
					clientSocketThread.start();
				}else if(state.equals("WAITING")){ // if thread have been used
					clientSocketThread.restart();
				}else if(state.equals("TERMINATED")){ // error
					System.out.println("스레드가 종료된 문제 발생");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector<ClientSocketThread> getThreadVector() {
		return threadVector;
	}

	public void setThreadVector(Vector<ClientSocketThread> threadVector) {
		this.threadVector = threadVector;
	}

	public void removeMapHashMapData(UserPosInfo userPosInfo) {
		
		
	}

	public void removeMapHashMapData(UserPosInfo userPosInfo, int mapIndex) {
		mapHashMap.get(mapIndex).setMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60, 1);
		synchronized(mapHashMap.get(mapIndex).getUserPosInfoVec()){
			mapHashMap.get(mapIndex).getUserPosInfoVec().remove(userPosInfo);
		}
		
	}

	public HashMap<Integer, MonsterGenThread> getMonsterGenThreadHashMap() {
		return monsterGenThreadHashMap;
	}
	
}
