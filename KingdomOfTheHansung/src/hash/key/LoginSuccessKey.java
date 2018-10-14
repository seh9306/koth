package hash.key;

import java.util.Vector;

import model.Item;
import model.Monster;
import model.UserPosInfo;

@SuppressWarnings("serial")
public class LoginSuccessKey extends SignalKey{
	private String id;
	private Vector<UserPosInfo> userPosInfoVec;
	private Vector<Monster> monsterVec;
	private int mapIndex;
	private int level;
	private int hp;
	private int mp;
	private int exp;
	private int gold;
	private String job;
	private Vector<Item> itemVec;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector<UserPosInfo> getUserPosInfoVec() {
		return userPosInfoVec;
	}

	public void setUserPosInfoVec(Vector<UserPosInfo> userPosInfoVec) {
		this.userPosInfoVec = userPosInfoVec;
	}

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
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

	public Vector<Monster> getMonsterVec() {
		return monsterVec;
	}

	public void setMonsterVec(Vector<Monster> monsterVec) {
		this.monsterVec = monsterVec;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
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

}
