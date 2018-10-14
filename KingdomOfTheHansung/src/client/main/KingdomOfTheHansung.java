package client.main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import client.thread.CharacterAnimationThread;
import client.thread.MonsterAnimationThread;
import client.thread.RecvThread;
import client.thread.SendThread;
import client.view.LoginPanel;
import client.view.MainFrame;
import client.view.controller.ViewManager;
import hash.key.SignalKey;
import model.Monster;

public class KingdomOfTheHansung {	
	private Socket socket;
	
	private final String IP = "112.150.217.60";
	private final int PORT = 8888;
	//112.150.217.60
	//192.168.0.6
	
	private ViewManager viewManager;
	
	private Vector<SignalKey> signalKeyVectorForSendThread;
	private HashMap<String, CharacterAnimationThread> characterAnimationThreadHashMap;
	private HashMap<Integer, MonsterAnimationThread> monsterAnimationThreadHashMap;
	
	// game configuration
	public KingdomOfTheHansung(){
		this.setSocket();
		this.setCollection(); 
		this.setView();
		this.setThread();
	}
	// setting game
	private void setThread(){
		new RecvThread(this).start();
		new SendThread(this).start();
	}
	
	private void setView(){
		viewManager = new ViewManager();
		new MainFrame(this);
	}
	
	private void setSocket(){
		try { // Connect to Server
			socket = new Socket(IP,PORT);
		} catch (IOException e) { // Connection fail...
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "서버와의 연결에 실패하였습니다. Class : KingdomOfTheHansung Constructor", "서버 연결 실패", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	private void setCollection(){
		characterAnimationThreadHashMap = new HashMap<String, CharacterAnimationThread>();
		monsterAnimationThreadHashMap = new HashMap<Integer, MonsterAnimationThread>();
		signalKeyVectorForSendThread = new Vector<SignalKey>();
	}
	
	// getters
	public Socket getSocket(){
		return socket;
	}

	public Vector<SignalKey> getSignalKeyVectorForSendThread() {
		return signalKeyVectorForSendThread;
	}

	public HashMap<String, CharacterAnimationThread> getCharacterAnimationThreadHashMap() {
		return characterAnimationThreadHashMap;
	}
	
	public ViewManager getViewManager() {
		return viewManager;
	}
	
	public HashMap<Integer, MonsterAnimationThread> getMonsterAnimationThreadHashMap() {
		return monsterAnimationThreadHashMap;
	}


}
