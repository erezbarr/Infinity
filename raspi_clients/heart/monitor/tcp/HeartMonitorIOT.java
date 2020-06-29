package il.co.ilrd.raspi_clients.heart.monitor.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;


public class HeartMonitorIOT  {
	protected static final String DB_NAME = "tadiran";

	protected static int serialNumber;// = 1;
	protected static String IP;// = "127.0.0.1";
	protected static int PORT;// = 50000;
	protected static final int BUFFER_SIZE = 4096;
	protected static SocketChannel clientSocket = null;
	private static RecieveMessage recieveMsesage = new RecieveMessage();
	private static SendMessage sendMsesage = new SendMessage();
	private static HeartrateSensor heartrateSensor = new HeartrateSensor();

	public static boolean isRunning = true;
	private static LinkedList<Thread> threads = new LinkedList<Thread>();
	protected static HashMap<String, IOTUpdateMessage> idToIOTMap = new HashMap<>(); 
	protected static PriorityBlockingQueue<IOTUpdateMessage> queue = new PriorityBlockingQueue<>();
	protected static Semaphore semQueueMsgs = new Semaphore(0);
	protected static Semaphore semNewUpdate = new Semaphore(0);
	protected static long RTT = 2000;

	
	public static void main(String[] args) throws IOException {
		System.out.println("??????????????");
		serialNumber = Integer.parseInt(args[0]);
		IP = args[1];
		PORT = Integer.parseInt(args[2]);
		
		threads.add(new Thread(recieveMsesage));
		threads.add(new Thread(sendMsesage));
		threads.add(new Thread(heartrateSensor));
//		try {
		System.out.println("howwwwwwwwwwww");
			initConnection();
//		} catch (IOException e) {
//			System.err.println("Connecting to server failed");
//			e.printStackTrace();
//			return;
//		}
		
	}

	private static void initConnection() throws IOException {
		System.out.println("jes");
		clientSocket = SocketChannel.open(new InetSocketAddress(IP, PORT));	
		for (Thread thread : threads) {
			System.out.println("fuck me");
			thread.start();
 		}
		System.out.println("Connecting heartMonitor to " + IP + " in port " + PORT);
	}

	public static void close() {
		isRunning = false;		
	}
}