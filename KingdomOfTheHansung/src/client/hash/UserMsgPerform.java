package client.hash;

import java.util.HashMap;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.view.MainPanel;
import hash.key.SignalKey;
import hash.key.UserMsgKey;
import model.UserPosInfo;
import value.CharacterAction;

public class UserMsgPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	private HashMap<String, CharacterAnimationThread> CharacterAnimationThreadHashMap;
	private CharacterAnimationThread characterAnimationThread;
	private MainPanel mainPanel;

	public UserMsgPerform(KingdomOfTheHansung koth) {
		this.koth = koth;
		CharacterAnimationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
	}

	@Override
	public void performAction(SignalKey signalKey) {
		UserMsgKey userMsgKey = (UserMsgKey) signalKey;
		String id = userMsgKey.getId();
		String msg = userMsgKey.getMsg();
		//int mapIndex = userMsgKey.getMapIndex();
		int posX = userMsgKey.getPosX();
		int posY = userMsgKey.getPosY();
		
		// ¸»Ç³¼±
		System.out.println("msg!!!!!!!!"+msg);
		characterAnimationThread = CharacterAnimationThreadHashMap.get(id);
		if (characterAnimationThread != null) {
			UserPosInfo userPosinfo = characterAnimationThread.getUserPosInfo();
			if (msg != null && !msg.equals("")) {
				mainPanel = koth.getViewManager().getMainPanel();
				
				UserPosInfo myPos = mainPanel.getUserPosInfo();
				if (characterAnimationThread.getMsgThread().getState().toString().equals("TIMED_WAITING")) {
					// System.out.println(myCharacterAnimationThread.getMsgThread().getState().toString());
					characterAnimationThread.getMsgThread().interrupt();
				}
				try {
					characterAnimationThread.getMsgQueue().put(id+":"+msg);
					mainPanel.getChatArea().append("    "+id+":"+msg+"\n");
					mainPanel.getChatArea().setCaretPosition(mainPanel.getChatArea().getDocument().getLength());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}

}
