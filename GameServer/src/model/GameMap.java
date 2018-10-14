package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import server.thread.MonsterGenThread;

public class GameMap implements Serializable{
	private Integer map[][];
	private int width, height;
	private Vector<UserPosInfo> userPosInfoVec;
	private Vector<Monster> monsterVector;
	private Monster[][] monsterMap;
	
	public GameMap(int width, int height){
		this.width = width;
		this.height = height;
		monsterMap = new Monster[height][width];
	}
	
	synchronized public void setMapValue(int posX, int posY, int value){
		map[posY][posX] = value;
	}
	
	synchronized public int getMapValue(int posX, int posY){
		return map[posY][posX];
	}

	public void setMap(Integer map[][]) {
		this.map = map;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Vector<UserPosInfo> getUserPosInfoVec() {
		return userPosInfoVec;
	}

	public void setUserPosInfoVec(Vector<UserPosInfo> userPosInfoVec) {
		this.userPosInfoVec = userPosInfoVec;
	}

	public Vector<Monster> getMonsterVector() {
		return monsterVector;
	}

	public void setMonsterVector(Vector<Monster> monsterVector) {
		this.monsterVector = monsterVector;
	}

	public Monster[][] getMonsterMap() {
		return monsterMap;
	}
}
