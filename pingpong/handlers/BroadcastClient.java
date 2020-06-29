package il.co.ilrd.pingpong.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingDeque;

public class BroadcastClient implements Runnable{
	private LinkedBlockingDeque<DatagramSocket> socketQueue= new LinkedBlockingDeque<>();
	private boolean BCTisRunning = true;
	private static final int WAIT_TIME = 5000;
	private static final int PORT = 50001;

	public void run() {
		BroadcastClient client = new BroadcastClient();
		client.runBroadcastClient();
		client.stoppingThread();
	}
	
	private void runBroadcastClient() {
		Runnable clientRunnable = new Runnable() {
			public void run() {
				BroadcastClientMethod();	
			}

		};
		for(int i = 0; i < 1; ++i)
			new Thread(clientRunnable).start();		
	}
	
	private void BroadcastClientMethod() {
		Message<Integer, Message<String, Void>> message = new PingPongMessage(0, "pong");
		try (DatagramSocket socket = new DatagramSocket()) {
			socketQueue.add(socket);	
			byte[] buf = new byte[1024];
			while(BCTisRunning) {
		        buf = Message.toByteArray(message);
		        DatagramPacket packet = new DatagramPacket(buf, buf.length, 
		        									InetAddress.getByName("127.255.255.255"), PORT);
		        socket.send(packet);	
		        System.out.print("BCT Say Pong, Server Say: ");
		        packet = new DatagramPacket(buf, buf.length);
		        socket.receive(packet);
		        PingPongMessage returnMessage = (PingPongMessage) Message.toObject(buf);
		        System.out.println(returnMessage.getData().getKey());
		        Thread.sleep(WAIT_TIME);
			}
			socket.close();
		} catch (Exception e) {
			if(BCTisRunning) {
				System.out.println(e);					
			}
			BCTisRunning = false;
		}
	}
	
	public void stoppingThread() {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(BCTisRunning) {
						if(input.readLine().equals("exit")) {
							BCTisRunning = false;					
							for(DatagramSocket iterDatagramSocket : socketQueue) {
								iterDatagramSocket.close();
							}
						}					
					}
				} catch (IOException e){
					System.out.println("stop" + e);
				}
			}
		};
		new Thread(stopRunnable).start();
	}
}
