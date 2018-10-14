package server.main;
/*
 * Title		: GameServer.java
 * Create Date	: 2017 11 14 , 17:00
 * Last Modify	: 2017 11 14 , 17:10
 * 
 * Made by Seho Park 
 */

public class GameServer {

	public static void main(String[] args) {
		System.out.println("Kingdom Of The Hansung Server start...");
		new Server().run();
	}

}
