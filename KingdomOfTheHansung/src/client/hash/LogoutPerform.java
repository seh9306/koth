package client.hash;

import java.util.HashMap;
import java.util.Vector;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.LogoutKey;
import hash.key.SignalKey;
import model.GameMap;
import model.UserPosInfo;

public class LogoutPerform implements SignalPerform {
	private HashMap<String, CharacterAnimationThread> characterAnimationThreadHashMap;
	private ViewManager viewManager;
	
	public LogoutPerform(KingdomOfTheHansung koth) {
		characterAnimationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		LogoutKey logoutKey = (LogoutKey) signalKey;
		String id = logoutKey.getId();
		System.out.println("·Î±×¾Æ¿ô ¾ÆÀÌµð : " + id);
		CharacterAnimationThread cat = characterAnimationThreadHashMap.get(id);
		cat.interrupt();
		MainPanel mp = viewManager.getMainPanel();
		GameMap gm = mp.getGm();
		Vector<UserPosInfo> userPosInfoVec = gm.getUserPosInfoVec();
		UserPosInfo userPosInfo = null;
		int size = userPosInfoVec.size();
		for(int i = 0; i < size; i++  ){
			userPosInfo = userPosInfoVec.get(i);
			if(id.equals(userPosInfo.getId())){
				userPosInfoVec.remove(i);
				break;
			}
		}
		if(userPosInfo !=null){
			System.out.println("³ª°«´Ù");
			gm.setMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60, 1);
		}
		mp.repaint();
	}

}
