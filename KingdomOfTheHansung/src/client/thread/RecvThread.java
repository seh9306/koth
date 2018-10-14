package client.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

import client.hash.ChangeMapSuccessPerform;
import client.hash.LoginSuccessPerform;
import client.hash.LogoutPerform;
import client.hash.MonsterAttackPerform;
import client.hash.MonsterDeadPerform;
import client.hash.MonsterMovingPerform;
import client.hash.MonsterMovingYPerform;
import client.hash.MonsterZenPerform;
import client.hash.SignalPerform;
import client.hash.TakeOffPerform;
import client.hash.UseItemPerform;
import client.hash.UserAppearPerform;
import client.hash.UserBlockPerform;
import client.hash.UserMovingPerform;
import client.hash.UserMovingYPerform;
import client.hash.UserMsgPerform;
import client.main.KingdomOfTheHansung;
import hash.key.SignalKey;
import value.ProtocolValue;

public class RecvThread extends Thread {
	private Socket recvSocket;
	private HashMap<Integer, SignalPerform> signalPerformHashMap;

	public RecvThread(KingdomOfTheHansung koth) {
		recvSocket = koth.getSocket();

		signalPerformHashMap = new HashMap<Integer, SignalPerform>();
		
		signalPerformHashMap.put(ProtocolValue.LOGIN_SUCCESS, new LoginSuccessPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USER_MOVING, new UserMovingPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USER_MOVINGY, new UserMovingYPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USER_APPEAR, new UserAppearPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USER_BLOCK, new UserBlockPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USER_MSG, new UserMsgPerform(koth));
		signalPerformHashMap.put(ProtocolValue.LOGOUT, new LogoutPerform(koth));
		signalPerformHashMap.put(ProtocolValue.MONSTER_ZEN, new MonsterZenPerform(koth));
		signalPerformHashMap.put(ProtocolValue.MONSTER_MOVING, new MonsterMovingPerform(koth));
		signalPerformHashMap.put(ProtocolValue.MONSTER_DEAD, new MonsterDeadPerform(koth));
		signalPerformHashMap.put(ProtocolValue.MONSTER_ATTACK, new MonsterAttackPerform(koth));
		signalPerformHashMap.put(ProtocolValue.MONSTER_MOVINGY, new MonsterMovingYPerform(koth));
		signalPerformHashMap.put(ProtocolValue.CHANGE_MAP_SUCCESS, new ChangeMapSuccessPerform(koth));
		signalPerformHashMap.put(ProtocolValue.USE_ITEM, new UseItemPerform(koth));
		signalPerformHashMap.put(ProtocolValue.TAKE_OFF, new TakeOffPerform(koth));
		
	}

	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(recvSocket.getInputStream());
			while (true) {
				try {
					SignalKey s = (SignalKey) ois.readObject();
					//System.out.println("프로토콜 정보 // "+s.getProtocol());
					SignalPerform performClass = signalPerformHashMap.get(s.getProtocol());
					performClass.performAction(s);
				} catch (ClassNotFoundException e) {					
					e.printStackTrace();
					System.out.println("이상한 값이 들어옴");
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
