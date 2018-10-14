package hash.key;

import model.Item;

public class MonsterDeadKey extends SignalKey{
	private int id;
	private int exp;
	private Item item;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}	
}
