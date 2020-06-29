package il.co.ilrd.raspi_clients.fridge.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;

public class FridgeIOT {
	protected static final String DB_NAME = "tadiran";

	protected static int serialNumber;// = 1;
	protected static String IP;// = "127.0.0.1";
	protected static int PORT;// = 8080;
	protected static int BUFFER_SIZE = 4096;
	protected static Socket socket;
	private static RecieveMessage recieveMsesage = new RecieveMessage();
	private static SendMessage sendMsesage = new SendMessage();
	private static DoorSensor doorSensor = new DoorSensor();

	public static boolean isRunning = true;
	private static LinkedList<Thread> threads = new LinkedList<Thread>();
	protected static HashMap<String, Semaphore> sensorLockMap = new HashMap<>(); 
	protected static HashMap<String, IOTUpdateMessage> idToIOTMap = new HashMap<>(); 
	protected static PriorityBlockingQueue<IOTUpdateMessage> queue = new PriorityBlockingQueue<>();
	protected static Semaphore semQueueMsgs = new Semaphore(0);
	protected static Semaphore semNewUpdate = new Semaphore(0);
	public static long RTT = 2000;
	
	public static void main(String[] args) {
		serialNumber = Integer.parseInt(args[0]);
		IP = args[1];
		PORT = Integer.parseInt(args[2]);
		threads.add(new Thread(recieveMsesage));
		threads.add(new Thread(sendMsesage));
		threads.add(new Thread(doorSensor));
		try {
			initConnection();
		} catch (IOException e) {
			System.err.println("Connecting to server failed");
			e.printStackTrace();
			return;
		} 
	}

	private static void initConnection() throws IOException {
		socket = new Socket(InetAddress.getByName(IP), PORT);
		for (Thread thread : threads) {
			thread.start();
		}
		System.out.println("Connecting fridgeIOT to " + IP + " in port " + PORT);
	}

	public static void close() {
		isRunning = false;		
	}
}