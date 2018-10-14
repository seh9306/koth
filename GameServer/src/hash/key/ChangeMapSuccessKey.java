package hash.key;

import java.util.Vector;

import model.Monster;
import model.UserPosInfo;

@SuppressWarnings("serial")
public class ChangeMapSuccessKey extends SignalKey{
	private Vector<UserPosInfo> userPosInfoVec;
	private Vector<Monster> monsterVec;
	private int mapIndex;
	private int posX, posY;
	
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

	public Vector<Monster> getMonsterVec() {
		return monsterVec;
	}

	public void setMonsterVec(Vector<Monster> monsterVec) {
		this.monsterVec = monsterVec;
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

}
