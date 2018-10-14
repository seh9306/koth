package client.hash;

import java.util.HashMap;
import java.util.Vector;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.thread.MonsterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.MonsterDeadKey;
import hash.key.SignalKey;
import model.GameMap;
import model.Monster;
import model.UserPosInfo;

public class MonsterDeadPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private HashMap<Integer, MonsterAnimationThread> monsterAnimationThreadHashMap;
	private ViewManager viewManager;

	public MonsterDeadPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		monsterAnimationThreadHashMap = koth.getMonsterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		MonsterDeadKey monsterDeadKey = (MonsterDeadKey) signalKey;
		int monId = monsterDeadKey.getId();
		int exp = monsterDeadKey.getExp();
		MainPanel mainPanel = viewManager.getMainPanel();
		
		if (exp == 0) { // 시체 처리!~!
			MonsterAnimationThread monsterAnimationThread = monsterAnimationThreadHashMap.get(monId);
			monsterAnimationThread.interrupt();
			MainPanel mp = viewManager.getMainPanel();
			GameMap gm = mp.getGm();

			Monster monster;
			Vector<Monster> monsterVector = gm.getMonsterVector();
			int size = monsterVector.size();
			for (int i = 0; i < size; i++) {
				monster = monsterVector.get(i);
				if (monId == monster.getId()) {
					gm.getMonsterMap()[monster.getPosY()/60][monster.getPosX()/60] = null;
					gm.setMapValue(monster.getPosX()/ 60, monster.getPosY() / 60, 1);
					monsterVector.remove(i);
					break;
				}
			}

			mp.repaint();

		} else {
			System.out.println(exp+"만큼의 경험치를 얻었습니다.");
			int totalExp = mainPanel.getExp() + exp;
			int level = mainPanel.getLevel();
			
			while(totalExp >= level * 100 ){
				totalExp -= level * 100;
				level++;
			}
			
			mainPanel.putItem(monsterDeadKey.getItem());			
			mainPanel.setExp(totalExp);
			mainPanel.setLevel(level);
			mainPanel.repaint();
		}

	}

}
