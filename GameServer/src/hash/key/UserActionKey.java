package hash.key;

import java.io.Serializable;

import model.UserPosInfo;

@SuppressWarnings("serial")
public class UserActionKey extends SignalKey implements Serializable{
	private String id;
	private int oldX, oldY;
	private int posX, posY, movingY;
	private int action;
	private int mapIndex;
	private boolean weapon;
	private boolean armor;
	
	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getOldX() {
		return oldX;
	}

	public void setOldX(int oldX) {
		this.oldX = oldX;
	}

	public int getOldY() {
		return oldY;
	}

	public void setOldY(int oldY) {
		this.oldY = oldY;
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
