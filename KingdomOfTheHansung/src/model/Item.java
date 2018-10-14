package model;

import java.io.Serializable;

public class Item implements Serializable{
	private String name;
	private int kind;
	private int effect;
	private char position;
	private String expain; 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public int getEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public char getPosition() {
		return position;
	}
	public void setPosition(char position) {
		this.position = position;
	}
	public String getExpain() {
		return expain;
	}
	public void setExpain(String expain) {
		this.expain = expain;
	}
	
	
}
