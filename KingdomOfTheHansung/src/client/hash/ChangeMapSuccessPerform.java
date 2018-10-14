package client.hash;

import java.util.HashMap;
import java.util.Vector;

import client.hash.SignalPerform;
import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.thread.MonsterAnimationThread;
import client.thread.MyCharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.ChangeMapSuccessKey;
import hash.key.SignalKey;
import model.GameMap;
import model.Monster;
import model.UserPosInfo;

public class ChangeMapSuccessPerform implements SignalPerform {

	private KingdomOfTheHansung koth;
	private HashMap<String, CharacterAnimationThread> characterAnimationThreadHashMap;
	private HashMap<Integer, MonsterAnimationThread> monsterAnimationThreadHashMap;
	private ViewManager viewManager;
	
	public ChangeMapSuccessPerform(KingdomOfTheHansung koth){
		this.koth = koth;
		characterAnimationThreadHashMap = koth.getCharacterAnimationThreadHashMap();
		monsterAnimationThreadHashMap = koth.getMonsterAnimationThreadHashMap();
		viewManager = koth.getViewManager();
	}
	
	@Override
	public void performAction(SignalKey signalKey) {
		ChangeMapSuccessKey changeMapSuccessKey = (ChangeMapSuccessKey) signalKey;
		characterAnimationThreadHashMap.clear();
		monsterAnimationThreadHashMap.clear();
			
		// �α��� �������� �Ȱ��� �ϰ�
		Vector<UserPosInfo> userPosInfoVec = changeMapSuccessKey.getUserPosInfoVec();
		Vector<Monster> monsterVec = changeMapSuccessKey.getMonsterVec();
		
		int size3 = monsterVec.size();
		
		for(int j = 0; j < size3; j++){
			Monster monster = monsterVec.get(j);
			System.out.println(monster.getId()+"//"+monster.getPosX() +" /ChangeMapaSuccess/"+ monster.getPosY());
		}
		
		MainPanel mainPanel = viewManager.getMainPanel();
		String id = mainPanel.getId();
		mainPanel.setMapIndex(changeMapSuccessKey.getMapIndex());
		mainPanel.setMap();
		
		GameMap gameMap = mainPanel.getGm();
		gameMap.setUserPosInfoVec(userPosInfoVec);
		gameMap.setMonsterVector(monsterVec);
		
		int size = userPosInfoVec.size();
		
		System.out.println("�����κ��� " + id +"���̵� �α����� �����Ͽ����ϴ�." + size);
		System.out.println("���� �ش� �ʿ� �����ϴ� ���� �� : " + size);
		// ���� ����
		for(int i = 0; i < size; i++){
			UserPosInfo userPosInfo = userPosInfoVec.get(i);
			
			if(id.equals(userPosInfo.getId())){
				MyCharacterAnimationThread myCharacterAnimationThread = new MyCharacterAnimationThread(koth);
				myCharacterAnimationThread.setUserPosInfo(userPosInfo);
				mainPanel.setUserPosInfo(userPosInfo); // ���񽺿� �並 �̾��� ���� ������ ������ ������Ų��.
				System.out.println("mapmove::"+userPosInfo.getX()+"//"+userPosInfo.getY());
				mainPanel.setMyCharacterAnimationThread(myCharacterAnimationThread);
				mainPanel.setUserPosInfoVec(userPosInfoVec);
				myCharacterAnimationThread.start();
			}else{
				CharacterAnimationThread OtherUserThreadForAnimation = new CharacterAnimationThread(koth);
				OtherUserThreadForAnimation.setUserPosInfo(userPosInfo); // setId !
				OtherUserThreadForAnimation.start();
				characterAnimationThreadHashMap.put(userPosInfo.getId(), OtherUserThreadForAnimation);
				gameMap.setMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60, 51);
			}
		}
		// ���� ����
		int size2 = monsterVec.size();
		mainPanel.setMonsterVec(monsterVec);
		for(int j = 0; j < size2; j++){
			Monster monster = monsterVec.get(j);
			System.out.println(monster.getId()+"//"+monster.getPosX() +" /ChangeMapaSuccess/"+ monster.getPosY());
			MonsterAnimationThread mat = new MonsterAnimationThread(koth);
			mat.setMonster(monster);
			//System.out.println(monster.getPosX() + "/monxy/"+monster.getPosY());
			monsterAnimationThreadHashMap.put(monster.getId(),mat);
			gameMap.setMapValue(monster.getPosX()/60,monster.getPosY()/60,52);
			mat.start();
		}
		
		mainPanel.requestFocus();
		mainPanel.grabFocus();
		
		//viewManager.validate();
		mainPanel.repaint();
	}

}
