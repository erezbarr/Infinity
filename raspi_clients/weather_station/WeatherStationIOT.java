package il.co.ilrd.raspi_clients.weather_station;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;


public class WeatherStationIOT {
	protected static final String DB_NAME = "tadiran";
	
	protected static final int serialNumber = 0;//1;
	protected static final String IP = null;//"127.0.0.1";
	protected static final int PORT = 0;//55555;
	protected static final int BUFFER_SIZE = 4096;
	protected static DatagramSocket socket;
	private static RecieveMessage recieveMsesage = new RecieveMessage();
	private static SendMessage sendMsesage = new SendMessage();
	private static HumiditySensor humiditySensor = new HumiditySensor();
	protected static HashMap<String, IOTUpdateMessage> idToIOTMap = new HashMap<>(); 
	protected static PriorityBlockingQueue<IOTUpdateMessage> queue = new PriorityBlockingQueue<>();
	protected static Semaphore semQueueMsgs = new Semaphore(0);
	protected static Semaphore semNewUpdate = new Semaphore(0);
	
	
	public static boolean isRunning = true;
	private static LinkedList<Thread> threads = new LinkedList<Thread>();
	protected static long RTT = 2000;
	public static void main(String[] args) {
		
		try {
			socket = new DatagramSocket();
			threads.add(new Thread(recieveMsesage));
			threads.add(new Thread(sendMsesage));
			threads.add(new Thread(humiditySensor));
			initConnection();
		} catch (IOException e) {
			System.err.println("Connecting to server failed");
			e.printStackTrace();
			return;
		}
	}

	private static void initConnection() throws IOException {
		for (Thread thread : threads) {
			thread.start();
		}
		System.out.println("Connecting WeatherStation to " + IP + " in port " + PORT);
	}

	public static void close() {
		isRunning = false;		
	}
}