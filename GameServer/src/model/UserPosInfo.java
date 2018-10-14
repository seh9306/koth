package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserPosInfo implements Serializable{
	private String id;
	private int x, y;
	private int movingX;
	private int movingY;
	private String msg;
	private boolean weapon;
	private boolean armor;
	
	public UserPosInfo(String id, int x, int y, int movingX, int movingY, String msg, boolean armor, boolean weapon) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.movingX = movingX;
		this.movingY = movingY;
		this.setMsg(msg);
		this.setArmor(armor);
		this.setWeapon(weapon);
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
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
