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
import value.CharacterAction;

public class UserMovingPerform implements SignalPerform {
	private HashMap<String, CharacterAnimationThread> CharacterAniamationThreadHashMap;
	private CharacterAnimationThread characterAnimationThread;

	private ViewManager viewManager;
	private MainPanel mainPanel;

	public UserMovingPerform(KingdomOfTheHansung koth) {
		CharacterAniamationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		UserActionKey userActionKey = (UserActionKey) signalKey;

		mainPanel = viewManager.getMainPanel();

		characterAnimationThread = CharacterAniamationThreadHashMap.get(userActionKey.getId());
		 
		if (characterAnimationThread != null) {
			int action = userActionKey.getAction();
			
			if (action < 4) { // 이동
				GameMap gm = mainPanel.getGm();
				characterAnimationThread.add(action);
				characterAnimationThread.setXY(userActionKey.getPosX(), userActionKey.getPosY());

				gm.setMapValue(userActionKey.getOldX() / 60, userActionKey.getOldY() / 60, 1);
				gm.setMapValue(userActionKey.getPosX() / 60, userActionKey.getPosY() / 60, 51);

			}else{ // 공격
				characterAnimationThread.add(CharacterAction.SPACE);
				characterAnimationThread.add(action);
			}
			characterAnimationThread.notifyThread();
		}

	}
}
