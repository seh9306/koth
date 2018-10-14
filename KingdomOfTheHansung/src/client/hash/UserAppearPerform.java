
package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import model.GameMap;
import model.UserPosInfo;

public class UserAppearPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private MainPanel mainPanel;
	private ViewManager viewManager;
	private HashMap<String,CharacterAnimationThread> characterAniamationThreadHashMap;
	
	public UserAppearPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		viewManager = koth.getViewManager();
		characterAniamationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		System.out.println("À¯Àú°¡ ³ªÅ¸³µ¾¹´Ï´Ù.");
		UserActionKey userActionKey = (UserActionKey) signalKey;
		UserPosInfo userPosInfo = new UserPosInfo(userActionKey.getId(),
				userActionKey.getPosX(),
				userActionKey.getPosY(),
				0,
				userActionKey.getMovingY(),null, userActionKey.isArmor(),userActionKey.isWeapon());
		
		CharacterAnimationThread thread = new CharacterAnimationThread(koth);
		thread.setUserPosInfo(userPosInfo);
		thread.start();
		
		if(mainPanel == null)
			mainPanel = viewManager.getMainPanel();
		GameMap gameMap = mainPanel.getGm();
		gameMap.setMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60, 51);
		
		characterAniamationThreadHashMap.put(userPosInfo.getId(), thread);
		System.out.println("µé¾î¿Â ¾ÆÀÌµð : " + userPosInfo.getId()+" xÁÂÇ¥ :" + userPosInfo.getX() + " yÁÂÇ¥ : " + userPosInfo.getY());
		mainPanel.getUserPosInfoVec().add(userPosInfo);
		mainPanel.repaint();
	}

}
