package hash.key;

import model.Item;

public class UseItemKey extends SignalKey{
	private Item item;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
