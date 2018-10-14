package client.hash;

import java.util.Vector;

import client.hash.SignalPerform;
import client.main.KingdomOfTheHansung;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.ChangeMapKey;
import hash.key.MonsterAttackKey;
import hash.key.SignalKey;
import value.ProtocolValue;

public class MonsterAttackPerform implements SignalPerform {
	private ViewManager viewManager;
	private Vector<SignalKey> sendData;

	public MonsterAttackPerform(KingdomOfTheHansung koth) {
		viewManager = koth.getViewManager();
		sendData = koth.getSignalKeyVectorForSendThread();
	}
	
	@Override
	public void performAction(SignalKey signalKey) {
		MonsterAttackKey monsterAttackKey = (MonsterAttackKey) signalKey;
		
		int dmg = monsterAttackKey.getDmg();
		
		MainPanel mainPanel = viewManager.getMainPanel();

		mainPanel.setHp(mainPanel.getHp()-dmg);
		
		if(mainPanel.getHp()<=0){
			mainPanel.setHp(1000);
			ChangeMapKey changeMapKey = new ChangeMapKey();
			changeMapKey.setProtocol(ProtocolValue.CHARACTER_DEAD);
			changeMapKey.setId(mainPanel.getId());
			
			changeMapKey.setOldMapIndex(mainPanel.getMapIndex());
			changeMapKey.setOldPosX(mainPanel.getUserPosInfo().getX());
			changeMapKey.setOldPosY(mainPanel.getUserPosInfo().getY());
			sendData.add(changeMapKey);
			mainPanel.getMyCharacterAnimationThread().interrupt();
		}
		
	}

}
