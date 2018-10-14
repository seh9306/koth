package hash.key;

public class AndroidAddFriendKey extends SignalKey {
    private String myID;
    private String friendID;

    public AndroidAddFriendKey(String myID, String friendID) {
        this.myID = myID;
        this.friendID = friendID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public String getMyID() {
        return myID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public String getFriendID() {
        return friendID;
    }
}