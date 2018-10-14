package model;

import java.util.Vector;

public class User {
	private String id;
	private String job;
	private int mapIndex;
	private int posX;
	private int posY;
	private int movingY;
	private int level;
	private int hp;
	private int mp;
	private int exp;
	private int gold;
	private boolean weapon;
	private boolean armor;
	private Vector<Item> itemVec;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public int getMapIndex() {
		return mapIndex;
	}
	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
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
	public int getMovingY() {
		return movingY;
	}
	public void setMovingY(int movingY) {
		this.movingY = movingY;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public Vector<Item> getItemVec() {
		return itemVec;
	}
	public void setItemVec(Vector<Item> itemVec) {
		this.itemVec = itemVec;
	}
	public boolean isArmor() {
		return armor;
	}
	public void setArmor(boolean armor) {
		this.armor = armor;
	}
	public boolean isWeapon() {
		return weapon;
	}
	public void setWeapon(boolean weapon) {
		this.weapon = weapon;
	}
	
	
	
}
