package model;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
	private String name;
    private int level;
    private int hp;
    private int mp;
    private List<String> items;

    public UserInfo(){
    	
    }
    
    public UserInfo(String name, int level, int hp, int mp, List<String> items) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.mp = mp;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public List<String> getItems() {
        return items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

}
