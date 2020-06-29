package il.co.ilrd.pingpong.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingDeque;

public class UdpClient implements Runnable{
	private LinkedBlockingDeque<DatagramSocket> socketQueue= new LinkedBlockingDeque<>();
	private boolean UDPisRunning = true;
	private static final int WAIT_TIME = 5000;
	private static final int PORT = 50000;

	public void run() {
		UdpClient udpClient = new UdpClient();
		udpClient.runUDPClient();
		udpClient.stoppingThread();	
	}
	
	private void runUDPClient() {
		Runnable clientRunnable = new Runnable() {
			public void run() {
				UDPClientMethod();	
			}

		};
		for(int i = 0; i < 1; ++i)
			new Thread(clientRunnable).start();		
	}
	
	private void UDPClientMethod() {
		Message<Integer, Message<String, Void>> message = new PingPongMessage(0, "pong");
		try (DatagramSocket socket = new DatagramSocket()) {
			socketQueue.add(socket);	
			byte[] buf = new byte[1024];
			while(UDPisRunning) {
		        buf = Message.toByteArray(message);
		        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), PORT);
		        socket.send(packet);			
		        System.out.print("UDP Say Pong, Server Say: ");
		        socket.receive(packet);
		        PingPongMessage returnMessage = (PingPongMessage) Message.toObject(buf);
		        System.out.println(returnMessage.getData().getKey());
		        Thread.sleep(WAIT_TIME);
			}
			socket.close();
		} catch (Exception e) {
			if(UDPisRunning) {
				System.out.println(e);					
			}
			UDPisRunning = false;
		}
	}

	public void stoppingThread() {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(UDPisRunning) {
						if(input.readLine().equals("exit")) {
							UDPisRunning = false;					
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