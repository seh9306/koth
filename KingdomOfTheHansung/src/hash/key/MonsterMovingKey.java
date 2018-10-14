package hash.key;

import model.Monster;

public class MonsterMovingKey extends SignalKey{
	private Monster monster;
	private int action;

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	

}
