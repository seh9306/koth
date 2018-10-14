package client.thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import client.main.KingdomOfTheHansung;
import hash.key.SignalKey;

public class SendThread extends Thread {
	private KingdomOfTheHansung koth;

	private Socket socketForSend;
	private Vector<SignalKey> SignalKeyVector;

	public SendThread(KingdomOfTheHansung koth) {
		this.koth = koth;
		SignalKeyVector = koth.getSignalKeyVectorForSendThread();
		socketForSend = koth.getSocket();
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socketForSend.getOutputStream());
			SignalKey key;
			while (true) {
				try {
					while(SignalKeyVector.size() != 0){
						key = SignalKeyVector.remove(0);
						oos.writeObject(key);
					}
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}