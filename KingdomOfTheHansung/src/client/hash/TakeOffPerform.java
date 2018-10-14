package client.hash;

import java.util.HashMap;

import client.hash.SignalPerform;
import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.SignalKey;
import hash.key.TakeOffKey;
import hash.key.UseItemKey;
import model.UserPosInfo;

public class TakeOffPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private MainPanel mainPanel;
	private ViewManager viewManager;
	private HashMap<String,CharacterAnimationThread> characterAniamationThreadHashMap;
	
	public TakeOffPerform(KingdomOfTheHansung koth) {
		// this.koth = koth;
		viewManager = koth.getViewManager();
		characterAniamationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		TakeOffKey takeOffKey = (TakeOffKey) signalKey;
		mainPanel = viewManager.getMainPanel();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+mainPanel.getId());
		if(takeOffKey.getItem().getPosition()=='.'){
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			String id = takeOffKey.getItem().getName();
			int kind = takeOffKey.getItem().getKind();	
			UserPosInfo userPosInfo = characterAniamationThreadHashMap.get(id).getUserPosInfo();
			switch (kind) {
			case 0:
				userPosInfo.setWeapon(false);
				break;
			case 1:
				userPosInfo.setArmor(false);
				break;
			}
			mainPanel.repaint();
		}else{
			mainPanel.putItem(takeOffKey.getItem());
			mainPanel.repaint();
		}		

		

	}

}
