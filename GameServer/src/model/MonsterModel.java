package model;

public class MonsterModel {
	private String monsterName; // 몬스터 이름
	private int hp;
	private int exp;
	private int dmg;
	
	public MonsterModel(String monsterName, int hp, int exp, int dmg) {
		super();
		this.monsterName = monsterName;
		this.hp = hp;
		this.exp = exp;
		this.dmg = dmg;
	}

	public String getMonsterName() {
		return monsterName;
	}

	public void setMonsterName(String monsterName) {
		this.monsterName = monsterName;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getDmg() {
		return dmg;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}
	
	
}
