package client.thread;

import java.util.Vector;

import client.main.KingdomOfTheHansung;
import client.view.MainPanel;
import model.Monster;
import value.CharacterAction;

public class MonsterAnimationThread extends Thread{
	private Monster monster;
	private int action;
	private MainPanel mainPanel;
	private Vector<Integer> actionVec;
	private int newX, newY;
	
	public MonsterAnimationThread(KingdomOfTheHansung koth) {
		mainPanel = koth.getViewManager().getMainPanel();
		actionVec = new Vector<Integer>();
	}

	synchronized public void notifyThread() {
		this.notify();
	}
	
	@Override
	public void run(){
		int check2 = 0;
		while (true) {
			//System.out.println(monster.getPosX() + "/monxy/"+monster.getPosY());
			int check;
			try {
				synchronized (this) {
					wait();	
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			while (actionVec.size() > 0) {
				action = actionVec.remove(0);
				if(action >= CharacterAction.MOVINGY){
					action %= CharacterAction.MOVINGY;
					monster.setMovingY(action);
					mainPanel.repaint();
					continue;
				}
				check = 0;
				check2 = 0;
				while (check < 8) {
					monster.setMovingY(action);
					check++;
					switch(action){
					case CharacterAction.UP:
						if (check % 2 == 0) {
							monster.setMovingX((monster.getMovingX() + 1) % 4);
							monster.setPosY(monster.getPosY() - 8);
						} else {
							monster.setPosY(monster.getPosY() - 7);
						}
						break;
					case CharacterAction.DOWN:
						if (check % 2 == 0) {
							monster.setMovingX((monster.getMovingX() + 1) % 4);
							monster.setPosY(monster.getPosY() + 8);
						} else
							monster.setPosY(monster.getPosY() + 7);
						break;
					case CharacterAction.RIGHT:
						if (check % 2 == 0) {
							monster.setMovingX((monster.getMovingX() + 1) % 4);
							monster.setPosX(monster.getPosX() + 8);
						} else
							monster.setPosX(monster.getPosX() + 7);
						break;
					case CharacterAction.LEFT:
						if (check % 2 == 0) {
							monster.setMovingX((monster.getMovingX() + 1) % 4);
							monster.setPosX(monster.getPosX() - 8);
						} else
							monster.setPosX(monster.getPosX() - 7);
						break;
					}
					mainPanel.repaint();
					try {
						if (check < 8) {
							sleep(40);
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
				/*monster.setPosX(newX);
				monster.setPosY(newY);*/
			}
		}

	}

	public void setXY(int x, int y) {
		newX = x;
		newY = y;
	}
	
	public void add(Integer direction) {
		actionVec.add(direction);
	}

	
	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

}
