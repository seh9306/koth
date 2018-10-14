package hash.key;

import model.Monster;

public class MonsterZenKey extends SignalKey{
	private Monster monster;
	
	public MonsterZenKey(int protocol, Monster monster) {
		super(protocol);
		this.monster = monster;
	}

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	
}
