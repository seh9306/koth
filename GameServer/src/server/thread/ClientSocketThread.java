package server.thread;
/*
 * Title		: ClientSocketThread.java
 * Create Date	: 2017 11 14 , 17:10
 * Last Modify	: 2017 11 17 , 16:27
 * 
 * Made by Seho Park 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import hash.key.LogoutKey;
import hash.key.SignalKey;
import model.User;
import model.UserPosInfo;
import server.dao.GameDAO;
import server.hash.SignalPerform;
import server.main.Server;
import server.main.ThreadPool;
import value.Debug;
import value.ProtocolValue;

public class ClientSocketThread extends Thread {
	private Server server;
	private String gameid = null;
	private Socket clientSocket;
	private ArrayList<Socket> socketArr;
	private ReentrantReadWriteLock readWriteLock;
	private ObjectOutputStream oos;
	private Vector<ClientSocketThread> clientSocketThreadVec;
	private UserPosInfo userPosInfo;
	
	private User user;

	private WriteLock writeLock;
	private ReadLock readLock;
	
	private int mapIndex;

	private HashMap<Integer, SignalPerform> signalPerformHashMap;

	public ClientSocketThread(Server server) {
		this.server = server;
		clientSocketThreadVec = server.getThreadVector();
	}

	public void setSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	synchronized public ObjectOutputStream getOos() {
		return oos;
	}

	public void restart() {
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void run() {
		socketArr = server.getSocketArr();
		readWriteLock = server.getLockForSocketArr();
		writeLock = readWriteLock.writeLock();
		readLock = readWriteLock.readLock();

		signalPerformHashMap = server.getSignalPerformHashMap();
		System.out.println("접속자 IP : "+clientSocket.getInetAddress().getHostAddress().toString());
		try {
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			while (true) {
				try {
					SignalKey s = (SignalKey) ois.readObject();
					SignalPerform performClass = signalPerformHashMap.get(s.getProtocol());
					performClass.performAction(this, s);

				} catch (IOException e1) { // client was disconnected.
					System.out.println(clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort() + ":::"
							+ gameid + "님께서 접속을 종료하셨습니다.");
					if(user!=null){
						String query = "update user_info "
								+ "set "
								+ "user_map_index = " + user.getMapIndex()
								+ ", user_posx = " + user.getPosX()
								+ ", user_posy = " + user.getPosY()
								+ ", user_movingy = " + user.getMovingY()
								+ ", user_level = " + user.getLevel()
								+ ", user_hp = " + user.getHp()
								+ ", user_mp = " + user.getMp()
								+ ", user_exp = " + user.getExp()
								+ ", user_gold = " + user.getGold()
								+ ", user_armor = ";
						if(user.isArmor())
							query += "1";
						else
							query += "0";
						query += ", user_weapon = ";
						if(user.isWeapon())
							query += "1";
						else
							query += "0";
						query +=
								", user_on = 0"
								+ " where user_id = '"+gameid+"'";
						GameDAO.getInstance().updateQuery(query);
					}
					writeLock.lock();
					clientSocketThreadVec.remove(this);
					socketArr.remove(this);
					writeLock.unlock();
					if (gameid != null) {
						server.removeMapHashMapData(userPosInfo,mapIndex);
						LogoutKey logoutKey = new LogoutKey(gameid);
						logoutKey.setProtocol(ProtocolValue.LOGOUT);
						int mapindex = mapIndex;
						readLock.lock();
						int size = clientSocketThreadVec.size();
						for (int i = 0; i < size; i++) {
							ClientSocketThread cst = clientSocketThreadVec.get(i);
							if (cst.getGameid() != null && cst.getMapIndex() == mapindex) {
								cst.getOos().writeObject(logoutKey);
							}
						}
						readLock.unlock();
					}
					
					gameid = null;
					ois.close();
					synchronized(oos){
						oos.close();
					}
					clientSocket.close();
					ThreadPool threadPool = ThreadPool.getInstance(null);
					threadPool.returnThread(this); // return thread to
													// ThreadPool.
					System.out.println("Number of user : " + threadPool.getNumOfuser());
					try {
						if (Debug.isDebug)
							System.out.println(this.getState().toString());
						synchronized (this) {

							wait(); // Thread is going to wait for another
									// client...

						}
						ois = new ObjectInputStream(clientSocket.getInputStream());
						oos = new ObjectOutputStream(clientSocket.getOutputStream());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public String getGameid() {
		return gameid;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public UserPosInfo getUserPosInfo() {
		return userPosInfo;
	}

	public void setUserPosInfo(UserPosInfo userPosInfo) {
		this.userPosInfo = userPosInfo;
		user.setPosX(userPosInfo.getX());
		user.setPosY(userPosInfo.getY());
	}

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
