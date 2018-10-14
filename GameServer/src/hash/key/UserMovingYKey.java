package hash.key;

public class UserMovingYKey extends SignalKey{
	private String id;
	private int movingY;
	private int mapIndex;

	public UserMovingYKey(int protocol, String id, int movingY, int mapIndex) {
		super(protocol);
		this.id = id;
		this.movingY = movingY;
		this.mapIndex = mapIndex;
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

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}


}
