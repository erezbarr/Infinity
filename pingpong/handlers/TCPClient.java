package il.co.ilrd.pingpong.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPClient implements Runnable {
	private final static int PORT_OF_TCP = 50004;
	private static final int WAIT_TIME = 5000;
	private boolean TCPisRunning = true;
	private SocketChannel client = null;
	private byte[] messageInBytes;

	@SuppressWarnings("unchecked")
	public void run() {
		PingPongMessage message1 = new PingPongMessage(0, "ping");
		ByteBuffer buffer = ByteBuffer.allocate(1024); 
		try {
			client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), PORT_OF_TCP));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stoppingThread();	
		
		while(TCPisRunning) {  
			try {
				messageInBytes = Message.toByteArray(message1);
		    	buffer = ByteBuffer.wrap(messageInBytes);
		    	buffer.clear();
				client.write(buffer);
		        System.out.print("TCP Say Ping, Server Say: ");
				buffer.clear();
				client.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Message<Integer, Message<String, Void>> message;
					message = (Message<Integer, Message<String, Void>>) 
					Message.toObject(buffer.array());
				System.out.println(message.getData().getKey());
				if ("exit" == message.getData().getKey()) {
					TCPisRunning = false;
				}
				Thread.sleep(WAIT_TIME);
			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}  
	}

	private void stoppingThread() {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(TCPisRunning) {
						if(input.readLine().equals("exit")) {
							TCPisRunning = false;					
							client.close();
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