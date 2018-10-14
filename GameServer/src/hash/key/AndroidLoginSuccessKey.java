package hash.key;

import java.util.List;

import model.Item;
import model.UserInfo;

public class AndroidLoginSuccessKey extends SignalKey{
	
	private UserInfo userInfo;
	private List<UserInfo> friendUserInfoList;
	private List<Item> itemList;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public List<UserInfo> getFriendUserInfoList() {
		return friendUserInfoList;
	}

	public void setFriendUserInfoList(List<UserInfo> friendUserInfoList) {
		this.friendUserInfoList = friendUserInfoList;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
 
}
