package client.hash;


import java.util.HashMap;
import java.util.Vector;

import client.main.KingdomOfTheHansung;
import client.thread.CharacterAnimationThread;
import client.thread.MonsterAnimationThread;
import client.thread.MyCharacterAnimationThread;
import client.view.MainPanel;
import client.view.controller.ViewManager;
import hash.key.LoginSuccessKey;
import hash.key.SignalKey;
import model.GameMap;
import model.Item;
import model.Monster;
import model.UserPosInfo;

public class LoginSuccessPerform implements SignalPerform {
	private KingdomOfTheHansung koth;
	
	public LoginSuccessPerform(KingdomOfTheHansung koth){
		this.koth = koth;
	}
	
	@Override
	public void performAction(SignalKey signalKey) {
		LoginSuccessKey loginSuccessKey = (LoginSuccessKey) signalKey;
		
		HashMap<String, CharacterAnimationThread> characterThreadHashMap = koth.getCharacterAnimationThreadHashMap();
		HashMap<Integer, MonsterAnimationThread> monsterThreadHashMap = koth.getMonsterAnimationThreadHashMap();
		Vector<UserPosInfo> userPosInfoVec = loginSuccessKey.getUserPosInfoVec();
		Vector<Monster> monsterVec = loginSuccessKey.getMonsterVec();
		String id = loginSuccessKey.getId();
		String job = loginSuccessKey.getJob();
		int hp = loginSuccessKey.getHp();
		int mp = loginSuccessKey.getMp();
		int exp = loginSuccessKey.getExp();
		int gold = loginSuccessKey.getGold();
		Vector<Item> itemVec = loginSuccessKey.getItemVec();
		
		ViewManager viewManager = koth.getViewManager();

		MainPanel mainPanel = new MainPanel(koth);
		
		viewManager.setMainPanel(mainPanel);
		viewManager.setContentPane(mainPanel);
		
		mainPanel.setMapIndex(loginSuccessKey.getMapIndex());
		mainPanel.setMap(); // draw map...
		mainPanel.setId(id);
		mainPanel.setLevel(loginSuccessKey.getLevel());
		mainPanel.setJob(job);
		mainPanel.setGold(gold);
		mainPanel.setExp(exp);
		mainPanel.setHp(hp);
		mainPanel.setMp(mp);
		mainPanel.setItemVec(itemVec);
		
		GameMap gameMap = mainPanel.getGm();
		gameMap.setUserPosInfoVec(userPosInfoVec);
		gameMap.setMonsterVector(monsterVec);
		
		int size = userPosInfoVec.size();
		
		System.out.println("서버로부터 " + id +"아이디 로그인이 성공하였습니다." + size);
		System.out.println("현재 해당 맵에 존재하는 유저 수 : " + size);
		// 유저 설정
		for(int i = 0; i < size; i++){
			UserPosInfo userPosInfo = userPosInfoVec.get(i);
			
			if(id.equals(userPosInfo.getId())){
				MyCharacterAnimationThread myCharacterAnimationThread = new MyCharacterAnimationThread(koth);
				myCharacterAnimationThread.setUserPosInfo(userPosInfo);
				mainPanel.setUserPosInfo(userPosInfo); // 서비스와 뷰를 이어줄 유저 본인의 정보를 공유시킨다.
				mainPanel.setMyCharacterAnimationThread(myCharacterAnimationThread);
				mainPanel.setUserPosInfoVec(userPosInfoVec);
				myCharacterAnimationThread.start();
			}else{
				CharacterAnimationThread OtherUserThreadForAnimation = new CharacterAnimationThread(koth);
				OtherUserThreadForAnimation.setUserPosInfo(userPosInfo); // setId !
				OtherUserThreadForAnimation.start();
				characterThreadHashMap.put(userPosInfo.getId(), OtherUserThreadForAnimation);
				gameMap.setMapValue(userPosInfo.getX()/60, userPosInfo.getY()/60, 51);
			}
		}
		// 몬스터 설정
		int size2 = monsterVec.size();
		mainPanel.setMonsterVec(monsterVec);
		for(int j = 0; j < size2; j++){
			Monster monster = monsterVec.get(j);
			MonsterAnimationThread mat = new MonsterAnimationThread(koth);
			mat.setMonster(monster);
			monsterThreadHashMap.put(monster.getId(),mat);
			gameMap.setMapValue(monster.getPosX()/60,monster.getPosY()/60,52);
			mat.start();
		}
		
		mainPanel.requestFocus();
		mainPanel.grabFocus();
		
		viewManager.validate();
		mainPanel.repaint();
		
	}

}
