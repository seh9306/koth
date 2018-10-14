package client.thread;

import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import client.main.KingdomOfTheHansung;
import client.thread.MyCharacterAnimationThread.MsgThread;
import client.view.MainPanel;
import model.Monster;
import model.UserPosInfo;
import value.CharacterAction;

public class CharacterAnimationThread extends Thread {
	private String id;
	private int action;
	private MainPanel mainPanel;
	private UserPosInfo userPosInfo;
	private int newX, newY;
	private Vector<Integer> userPosInfoVec;
	private boolean isSpace = false;

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
			while(true){
				try {
					String msg = msgQueue.take();
					if(msg != null && !msg.equals("")){
						System.out.println(msg+"메세지가 추가");
						userPosInfo.setMsg(msg);
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
	
	public CharacterAnimationThread(KingdomOfTheHansung koth) {
		mainPanel = koth.getViewManager().getMainPanel();
		userPosInfoVec = new Vector<Integer>();
		msgThread = new MsgThread();
		msgThread.start();
	}

	synchronized public void notifyThread() {
		this.notify();
	}

	public String getGameId() {
		return id;
	}

	@Override
	public void run() {
		int check2 = 0;
		while (true) {
			int check;
			try {
				sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
			while (userPosInfoVec.size() > 0) {
				// System.out.println("안비엇다.");
				action = userPosInfoVec.remove(0);
				if(action == CharacterAction.SPACE){
					isSpace = true;
					continue;
				}else if(action >= CharacterAction.MOVINGY){
					action %= CharacterAction.MOVINGY;
					userPosInfo.setMovingY(action);
					mainPanel.repaint();
					continue;
				}
				check = 0;
				check2 = 0;
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
					case CharacterAction.DOWN_SPACE:
					case CharacterAction.RIGHT_SPACE:
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
								if(check > 3){
									action -= 4;
									userPosInfo.setMovingY(action);
									mainPanel.repaint();
									isSpace = false;
									break;
								}
								sleep(100);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}

				}

			}
			if (check2 == 0) {
				// System.out.println(newX+" /new/ "+ newY);
				check2++;
				userPosInfo.setX(newX);
				userPosInfo.setY(newY);
			}
		}

	}

	public UserPosInfo getUserPosInfo() {
		return userPosInfo;
	}

	public void setUserPosInfo(UserPosInfo userPosInfo) {
		this.userPosInfo = userPosInfo;
		this.id = userPosInfo.getId();
		this.newX = userPosInfo.getX();
		this.newY = userPosInfo.getY();
	}

	public void setXY(int x, int y) {
		// if(!mainPanel.getId().equals(id))
		// System.out.println(x+"/setXY/"+y);
		newX = x;
		newY = y;
	}

	public void add(Integer direction) {
		userPosInfoVec.add(direction);
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

}
