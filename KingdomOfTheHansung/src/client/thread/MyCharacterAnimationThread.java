package client.thread;

import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import client.main.KingdomOfTheHansung;
import client.view.MainPanel;
import hash.key.ChangeMapKey;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import hash.key.UserMovingYKey;
import model.GameMap;
import model.Monster;
import model.UserPosInfo;
import value.CharacterAction;
import value.ProtocolValue;

public class MyCharacterAnimationThread extends Thread {
	private MainPanel mainPanel;
	private GameMap gameMap;
	private UserPosInfo userPosInfo;
	private int action;
	private Vector<SignalKey> sendData;
	private String gameId;
	private UserActionKey userActionKey;
	private boolean isSpace = false;
	private boolean isMapChange = false;
	private Monster[][] monsterMap;
	
	private MsgThread msgThread;
	private BlockingQueue<String> msgQueue;
	
	public BlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

	public MsgThread getMsgThread() {
		return msgThread;
	}

	public class MsgThread extends Thread{
		MsgThread(){
			msgQueue = new ArrayBlockingQueue<String>(10);
		}
		
		@Override
		public void run(){
			String msg;
			while(true){
				try {
					msg = msgQueue.take();
					System.out.println("수신을 해버렸따.");
					if(msg != null && !msg.equals("")){
						System.out.println(msg+"메세지가 추가");
						userPosInfo.setMsg(gameId+":"+msg);
						mainPanel.repaint();
						sleep(3000);
						userPosInfo.setMsg(null);
						System.out.println("말풍선 사라진다.");
						mainPanel.repaint();
					}
				} catch (InterruptedException e) {
					userPosInfo.setMsg(null);
					mainPanel.repaint();
					e.printStackTrace();
					System.out.println("메세지가 또 들어옴");
				}
			}
		}
	}
	
	public MyCharacterAnimationThread(KingdomOfTheHansung koth) {
		mainPanel = koth.getViewManager().getMainPanel();
		gameMap = mainPanel.getGm();
		gameId = mainPanel.getId();
		sendData = koth.getSignalKeyVectorForSendThread();
		monsterMap = mainPanel.getMonsterMap();
		msgThread = new MsgThread();
		msgThread.start();
		
	}
	
	public void notifySpace(){
		isSpace = true;
	}
	
	public void notifyMsg(){
		
	}
	
	@Override
	public void run(){
		while(true){
			synchronized(this){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					if(isMapChange)
						return;
					continue;
				}
			}
			if(isSpace||!collapse()){
				if(isSpace)
					action += 4;
				moveCharacter();
				System.out.println(userPosInfo.getX()+"/x y/"+userPosInfo.getY());
				userActionKey = new UserActionKey();
				userActionKey.setProtocol(ProtocolValue.USER_MOVING);
				userActionKey.setId(gameId);
				userActionKey.setPosX(userPosInfo.getX());
				userActionKey.setPosY(userPosInfo.getY());
				userActionKey.setMovingY(userPosInfo.getMovingY());
				userActionKey.setAction(action);
				userActionKey.setMapIndex(mainPanel.getMapIndex());
				if(!isMapChange)
					sendData.add(userActionKey);
				
				if(isSpace){
					action -= 4;
					userPosInfo.setMovingY(action);
					mainPanel.repaint();
					isSpace = false;
				}
			}
		}
	}

	public UserPosInfo getUserPosInfo() {
		return userPosInfo;
	}

	public void setUserPosInfo(UserPosInfo userPosInfo) {
		this.userPosInfo = userPosInfo;
		
	}
	
	public void moveCharacter() {
		int check;

		check = 0;
		while (check < 8) {
			userPosInfo.setMovingY(action);
			check++;
			switch(action){
			case CharacterAction.UP:
				if (check % 2 == 0) {
					userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
					userPosInfo.setY(userPosInfo.getY() - 8);
				} else {
					userPosInfo.setY(userPosInfo.getY() - 7);
				}
				break;
			case CharacterAction.DOWN:
				if (check % 2 == 0) {
					userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
					userPosInfo.setY(userPosInfo.getY() + 8);
				} else
					userPosInfo.setY(userPosInfo.getY() + 7);
				break;
			case CharacterAction.RIGHT:
				if (check % 2 == 0) {
					userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
					userPosInfo.setX(userPosInfo.getX() + 8);
				} else
					userPosInfo.setX(userPosInfo.getX() + 7);
				break;
			case CharacterAction.LEFT:
				if (check % 2 == 0) {
					userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
					userPosInfo.setX(userPosInfo.getX() - 8);
				} else
					userPosInfo.setX(userPosInfo.getX() - 7);
				break;
			case CharacterAction.UP_SPACE:
				userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
				break;
			case CharacterAction.DOWN_SPACE:
				userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
				break;
			case CharacterAction.RIGHT_SPACE:
				userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
				break;
			case CharacterAction.LEFT_SPACE:
				userPosInfo.setMovingX((userPosInfo.getMovingX() + 1) % 4);
				break;
			}
			mainPanel.repaint();
			try {
				if (check < 8) {
					if(!isSpace){
						sleep(40);
					}else{
						if(check >3)
							break;
						sleep(90);
					}
				}
			} catch (InterruptedException e) {
				System.out.println("블럭당햇다.");
				break;
				//e.printStackTrace();
			}
			
		}
		if(gameMap.getMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60) == 49){
			ChangeMapKey changeMapKey = new ChangeMapKey();
			changeMapKey.setProtocol(ProtocolValue.CHANGE_MAP);
			changeMapKey.setId(gameId);
			changeMapKey.setOldMapIndex(mainPanel.getMapIndex());
			if(mainPanel.getMapIndex() == 0){
				changeMapKey.setMapIndex(1);
				//if(userPosInfo.getX()/60 < 12 )
			}else{
				changeMapKey.setMapIndex(0);
			}
			
			changeMapKey.setOldPosX(userPosInfo.getX());
			changeMapKey.setOldPosY(userPosInfo.getY());
			sendData.add(changeMapKey);
			interrupt();
			isMapChange = true;
		}
		
	}

	public void setAction(int keyCode) {
		this.action = keyCode-37;
	}

	public void notifyThread() {
		synchronized (this) {
			notify();
		}		
	}
	
	private boolean collapse(){
		int posX = userPosInfo.getX() / 60;
		int posY = userPosInfo.getY() / 60;
		/*UserPosInfo posInfo = new UserPosInfo(
				gameId,
				userPosInfo.getX(),
				userPosInfo.getY(),
				userPosInfo.getMovingX(),
				userPosInfo.getMovingY()
				);*/
		switch (action) {
		case CharacterAction.UP:
			if (posY <= 0 || gameMap.getMapValue(posX, posY - 1) > 50 ) {
				userPosInfo.setMovingY(action);
				sendData.add(new UserMovingYKey(ProtocolValue.USER_MOVINGY,gameId,action,mainPanel.getMapIndex()));
				mainPanel.repaint();
				return true;
			}
			//posInfo.setY((posY - 1) * 60);
			break;
		case CharacterAction.DOWN:
			if ( posY >= gameMap.getHeight()-1 || gameMap.getMapValue(posX, posY + 1) > 50) {
				userPosInfo.setMovingY(action);
				sendData.add(new UserMovingYKey(ProtocolValue.USER_MOVINGY,gameId,action,mainPanel.getMapIndex()));
				mainPanel.repaint();
				return true;
			}
			//posInfo.setY((posY + 1) * 60);
			break;
		case CharacterAction.RIGHT:
			if ( posX >= gameMap.getWidth()-1 || gameMap.getMapValue(posX + 1, posY) > 50 ) {
				userPosInfo.setMovingY(action);
				sendData.add(new UserMovingYKey(ProtocolValue.USER_MOVINGY,gameId,action,mainPanel.getMapIndex()));
				mainPanel.repaint();
				return true;
			}
			//posInfo.setX((posX + 1) * 60);
			break;
		case CharacterAction.LEFT:
			if ( posX <= 0 || gameMap.getMapValue(posX - 1, posY) > 50 ) {
				userPosInfo.setMovingY(action);
				sendData.add(new UserMovingYKey(ProtocolValue.USER_MOVINGY,gameId,action,mainPanel.getMapIndex()));
				mainPanel.repaint();
				return true;
			}
			//posInfo.setX((posX - 1) * 60);
			break;
		default:
			return true;
		}
		return false;
	}

	public void setIsMapChange(){
		isMapChange = false;
	}
}
