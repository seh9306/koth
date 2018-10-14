package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import hash.key.SignalKey;
import hash.key.UserMovingYKey;
import model.UserPosInfo;
import value.CharacterAction;

public class UserMovingYPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private HashMap<String, CharacterAnimationThread> CharacterAnimationThreadHashMap;
	private MainPanel mainPanel;
	private CharacterAnimationThread characterAnimationThread;

	public UserMovingYPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		CharacterAnimationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		UserMovingYKey userMovingYKey = (UserMovingYKey) signalKey;
		System.out.println(userMovingYKey.getId() + "´ÔÀÇ ¿ÍÀÌÆÛÆû!");
		characterAnimationThread = CharacterAnimationThreadHashMap.get(userMovingYKey.getId());
		if (characterAnimationThread != null) {
			UserPosInfo userPosinfo = characterAnimationThread.getUserPosInfo();

			characterAnimationThread.add(CharacterAction.MOVINGY + userMovingYKey.getMovingY());
		}

	}

}
