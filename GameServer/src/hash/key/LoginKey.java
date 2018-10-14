package hash.key;

public class LoginKey extends SignalKey{
	private String id;
	private String pw;

	public LoginKey(int protocol, String id, String pw) {
		super(protocol);
		this.id = id;
		this.pw = pw;
	}
	
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
