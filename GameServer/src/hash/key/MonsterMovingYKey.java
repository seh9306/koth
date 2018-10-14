package hash.key;

public class MonsterMovingYKey extends SignalKey{
	private int id;
	private int movingY;

	public MonsterMovingYKey(int protocol, int id, int movingY) {
		super(protocol);
		this.id = id;
		this.movingY = movingY;
	}

	public int getMovingY() {
		return movingY;
	}

	public void setMovingY(int movingY) {
		this.movingY = movingY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
