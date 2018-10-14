package hash.key;

public class ChangeMapKey extends SignalKey {
	private String id;
	private int mapIndex;
	private int oldMapIndex;
	private int oldPosX;
	private int oldPosY;

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getOldMapIndex() {
		return oldMapIndex;
	}

	public void setOldMapIndex(int oldMapIndex) {
		this.oldMapIndex = oldMapIndex;
	}

	public int getOldPosY() {
		return oldPosY;
	}

	public void setOldPosY(int oldPosY) {
		this.oldPosY = oldPosY;
	}

	public int getOldPosX() {
		return oldPosX;
	}

	public void setOldPosX(int oldPosX) {
		this.oldPosX = oldPosX;
	}

}
