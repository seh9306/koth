package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.MonsterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.MonsterZenKey;
import hash.key.SignalKey;
import model.GameMap;
import model.Monster;

public class MonsterZenPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private HashMap<Integer,MonsterAnimationThread> monsterThreadHashMap;
	private ViewManager viewManager;
	private MainPanel mainPanel;
	private Monster monsterMap[][];
	
	public MonsterZenPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		monsterThreadHashMap = koth.getMonsterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}
	
	@Override
	public void performAction(SignalKey signalKey) {
		MonsterZenKey monsterZenKey = (MonsterZenKey) signalKey;
		Monster monster = monsterZenKey.getMonster();
		
		MonsterAnimationThread mat = new MonsterAnimationThread(koth);
		mat.setMonster(monster);
		mat.start();
		
		if(mainPanel == null){
			mainPanel = viewManager.getMainPanel();
			monsterMap = mainPanel.getMonsterMap();
		}
		
		GameMap gameMap = mainPanel.getGm();
		gameMap.setMapValue(monster.getPosX()/60,monster.getPosY()/60,52);
		
		monsterThreadHashMap.put(monster.getId(),mat);
		
		monsterMap[monster.getPosY()/60][monster.getPosX()/60] = monster;
		
		mainPanel.getMonsterVec().add(monster);
		mainPanel.repaint();
		
		//System.out.println(monster.getPosX() + "/zen/"+monster.getPosY());
	}

}
