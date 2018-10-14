package hash.key;

public class UserMsgKey extends SignalKey{
	private String id;
	private String msg;
	private int mapIndex;
	private int posX;
	private int posY;
	
	public UserMsgKey(){
		
	}
	
	public UserMsgKey(UserMsgKey u){
		this.setProtocol(u.getProtocol());
		this.id = u.id;
		this.msg = u.msg;
		this.mapIndex = u.mapIndex;
		this.posX = u.posX;
		this.posY = u.posY;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getMapIndex() {
		return mapIndex;
	}
	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	

}
