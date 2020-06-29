package il.co.ilrd.pingpong.handlers;

import java.io.IOException;

import il.co.ilrd.pingpong.handlers.Server.WrongUseOfSelector;

public class runMe {
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, WrongUseOfSelector {
		
		System.out.println("Server Starting");
		Server server = new Server();
		server.addTcpConnection(50004);
		server.addUdpConnection(50000);
		server.addBroadcastConnection(50001);
		new Thread(server).start();

		
		Thread.sleep(2000);

		System.out.println("TCP Starting");
		new Thread(new TCPClient()).start();

		Thread.sleep(2000);

		System.out.println("UDP Starting");
		new Thread(new UdpClient()).start();

		Thread.sleep(2000);

		System.out.println("BCT Starting");
		new Thread(new BroadcastClient()).run();
	}
}