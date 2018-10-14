package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.thread.MyCharacterAnimationThread;
import client.view.MainPanel;
import hash.key.SignalKey;
import hash.key.UserActionKey;
import model.UserPosInfo;

public class UserBlockPerform implements SignalPerform {
	
	private MainPanel mp;
	HashMap<String, CharacterAnimationThread> characterThreadHashMap;
	private KingdomOfTheHansung koth;
	
	public UserBlockPerform(KingdomOfTheHansung koth){
		this.koth = koth;
		characterThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		System.out.println("User Block!!!");
		UserActionKey userMovingKey = (UserActionKey) signalKey;
		mp = koth.getViewManager().getMainPanel();
		MyCharacterAnimationThread cat = mp.getMyCharacterAnimationThread();
		cat.interrupt();
		System.out.println(userMovingKey.getPosX()+"/BlockPerform/"+userMovingKey.getPosY());
		UserPosInfo wrongPos = mp.getUserPosInfo();
		wrongPos.setX(userMovingKey.getPosX()/60*60);
		wrongPos.setY(userMovingKey.getPosY()/60*60);
		wrongPos.setMovingX(0);
		wrongPos.setMovingY(userMovingKey.getMovingY());
		
		mp.repaint();
		//mp.getThread().interrupt();
	} 
	

}
