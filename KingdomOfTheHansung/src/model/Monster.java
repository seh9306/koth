package model;

import java.io.Serializable;

public class Monster implements Serializable {
	private int id; // 고유 값
	private String monsterName; // 몬스터 이름
	private int posX, posY;
	private int hp;
	private int movingY, movingX;
	private String aggroId = null;
	private int exp;
	private int dmg;
	private int skillY;
	
	public Monster(int id, String monsterName, int posX, int posY, int hp, int movingY, int movingX, int exp, int dmg) {
		this.id = id;
		this.monsterName = monsterName;
		this.posX = posX;
		this.posY = posY;
		this.hp = hp;
		this.movingY = movingY;
		this.movingX = movingX;
		this.exp = exp;
		this.dmg = dmg;
		skillY = -1;
	}
	
	public Monster(Monster m){
		this.id = m.id;
		this.aggroId = m.aggroId;
		this.monsterName = m.monsterName;
		this.posX = m.posX;
		this.posY = m.posY;
		this.hp = m.hp;
		this.movingY = m.movingY;
		this.movingX = m.movingX;
		this.exp = m.exp;
		this.dmg = m.dmg;
		skillY = -1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMonsterName() {
		return monsterName;
	}
	public void setMonsterName(String monsterName) {
		this.monsterName = monsterName;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getMovingY() {
		return movingY;
	}
	public void setMovingY(int movingY) {
		this.movingY = movingY;
	}
	public int getMovingX() {
		return movingX;
	}
	public void setMovingX(int movingX) {
		this.movingX = movingX;
	}
	public String getAggroId() {
		return aggroId;
	}
	public void setAggroId(String aggroId) {
		this.aggroId = aggroId;
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

	public int getSkillY() {
		return skillY;
	}

	public void setSkillY(int skillY) {
		this.skillY = skillY;
	}
	
}
