package il.co.ilrd.chatserver;

import java.io.IOException;

import il.co.ilrd.chatserver.Server.WrongUseOfSelector;

public class runMe {
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, WrongUseOfSelector {
		
		System.out.println("Server Starting");
		Server server = new Server();
		server.addTcpConnection(50000);

		new Thread(server).start();

		
		Thread.sleep(2000);

		System.out.println("Client1 Starting");
		new Thread(new Client()).start();

		Thread.sleep(2000);

		System.out.println("Client2 Starting");
		new Thread(new Client()).start();
		
		Thread.sleep(2000);

		System.out.println("Client3 Starting");
		new Thread(new Client()).run();		
	}
}