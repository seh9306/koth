package hash.key;

import java.io.Serializable;

public class SignalKey implements Serializable{
	private int protocol;

	public SignalKey() {

	}

	public SignalKey(int protocol) {
		this.protocol = protocol;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
}
