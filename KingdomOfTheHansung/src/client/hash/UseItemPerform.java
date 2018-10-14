package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.SignalKey;
import hash.key.UseItemKey;
import model.UserPosInfo;

public class UseItemPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private MainPanel mainPanel;
	private ViewManager viewManager;
	private HashMap<String,CharacterAnimationThread> characterAniamationThreadHashMap;
	
	public UseItemPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		viewManager = koth.getViewManager();
		characterAniamationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		UseItemKey useItemKey = (UseItemKey) signalKey;
		
		String id = useItemKey.getItem().getName();
		int kind = useItemKey.getItem().getKind();
		
		UserPosInfo userPosInfo = characterAniamationThreadHashMap.get(id).getUserPosInfo();
		
		switch(kind){
		case 0:
			userPosInfo.setWeapon(true);
			viewManager.getMainPanel().repaint();
			break;
		case 1:
			userPosInfo.setArmor(true);
			viewManager.getMainPanel().repaint();
			break;
		}
	}

}
