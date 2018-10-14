package hash.key;

public class LogoutKey extends SignalKey{
	private String id;

	public LogoutKey(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
