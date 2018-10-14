package server.main;

/*
 * Title		: ThreadPool.java
 * Create Date	: 2017 11 14 , 17:05
 * Last Modify	: 2017 11 17 , 16:27
 * 
 * Made by Seho Park 
 */

import java.util.Vector;

import server.thread.ClientSocketThread;
import value.Debug;

public class ThreadPool {
	private final int MAXTHREADNUM = 50;

	private static ThreadPool threadPool = null;
	private Vector<ClientSocketThread> threadVector;
	private int numOfThread = MAXTHREADNUM;

	// singleton
	private ThreadPool(Server server) { // ���� ���� ������ ������ Ǯ
		threadVector = new Vector<ClientSocketThread>();
		for (int i = 0; i < numOfThread; i++) {
			threadVector.add(new ClientSocketThread(server));
		}
		numOfThread--;

		if (Debug.isDebug) {
			System.out.println("����� ��� ������ ���� :: �����ο� üũ");
			new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(numOfThread);
					}
				}
			}.start();
		}
	}

	public static ThreadPool getInstance(Server server) { // ���� ���� �����常�� �ν��Ͻ���
															// ȣ���ϱ� ������ ����ȭ�� �ʿ�
															// ����.
		if (threadPool == null) {
			threadPool = new ThreadPool(server);
		}

		return threadPool;
	}

	public int getNumOfuser() {
		return MAXTHREADNUM - numOfThread - 1;
	}

	// all write lock.
	public ClientSocketThread getThread() {
		synchronized (this) {
			return threadVector.remove(numOfThread--);
		}
	}

	public void returnThread(ClientSocketThread tmp) {
		synchronized (this) {
			numOfThread++;
			threadVector.add(tmp);
		}
	}
}
