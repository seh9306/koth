package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.MonsterAnimationThread;
import hash.key.MonsterMovingYKey;
import hash.key.SignalKey;
import value.CharacterAction;

public class MonsterMovingYPerform implements SignalPerform {
	private HashMap<Integer, MonsterAnimationThread> monsterAniamationThreadHashMap;
	private MonsterAnimationThread monsterAnimationThread;

	public MonsterMovingYPerform(KingdomOfTheHansung koth) {
		monsterAniamationThreadHashMap = koth.getMonsterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		MonsterMovingYKey monsterMovingYKey = (MonsterMovingYKey) signalKey;
		monsterAnimationThread = monsterAniamationThreadHashMap.get(monsterMovingYKey.getId());
		monsterAnimationThread.add(CharacterAction.MOVINGY+monsterMovingYKey.getMovingY());
		monsterAnimationThread.notifyThread();
	}

}
