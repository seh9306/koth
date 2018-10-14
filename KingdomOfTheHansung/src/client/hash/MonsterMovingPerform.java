package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.MonsterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.MonsterMovingKey;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import model.GameMap;
import model.Monster;

public class MonsterMovingPerform implements SignalPerform {
	private HashMap<Integer, MonsterAnimationThread> monsterAniamationThreadHashMap;
	private MonsterAnimationThread monsterAnimationThread;

	private ViewManager viewManager;
	private MainPanel mainPanel;
	private Monster monsterMap[][];

	public MonsterMovingPerform(KingdomOfTheHansung koth) {
		monsterAniamationThreadHashMap = koth.getMonsterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		MonsterMovingKey monsterMovingKey = (MonsterMovingKey) signalKey;

		mainPanel = viewManager.getMainPanel();
		monsterMap = mainPanel.getMonsterMap();
	
		Monster monster = monsterMovingKey.getMonster();

		monsterAnimationThread = monsterAniamationThreadHashMap.get(monster.getId());

		if (monsterAnimationThread != null) {
			GameMap gm = mainPanel.getGm();
			Monster oldMonster = monsterAnimationThread.getMonster();
			//System.out.println(oldMonster.getPosX()+"//////"+oldMonster.getPosY());
			//System.out.println(oldMonster.getId()+"/old/"+oldMonster.getPosX() + "/oldxy/"+oldMonster.getPosY());
			//System.out.println(monster.getId()+"/new/"+monster.getPosX() + "/monxy/"+monster.getPosY());
			gm.setMapValue(oldMonster.getPosX()/60 , oldMonster.getPosY()/60, 1);
			monsterMap[oldMonster.getPosY()/60][oldMonster.getPosX()/60] = null;
			gm.setMapValue(monster.getPosX() /60, monster.getPosY() /60, 52);
			monsterMap[monster.getPosY()/60][monster.getPosX()/60] = monster;
			
			monsterAnimationThread.add(((MonsterMovingKey) signalKey).getAction());
			//monsterAnimationThread.setXY(monster.getPosX(), monster.getPosY());
			monsterAnimationThread.notifyThread();
			
		}

	}

}
