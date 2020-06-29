package il.co.ilrd.raspi_clients.fridge.http;

import java.rmi.server.UID;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;
import il.co.ilrd.raspi_clients.heart.monitor.tcp.SendMessage;

public class DoorSensor implements Runnable {
	int msgCounter = 0;
	private final static String SENSOR_NAME = "doorSensor";
	private static final String ERROR = "ERROR ";
	private static final String UPDATE = "UPDATE ";
	private static final String AT = "@";
	private static final String ERROR_MSG = "door is open!";
	private static final String UPDATE_MSG = "door closed";
	private static final int ERROT_REPEAT = 3;
	private static final int UPDATE_REPEAT = 1;
	private static final int SENDING_DELAY = 1;	
	
	
	public void run() {
		while(FridgeIOT.isRunning) {
			try {
				Thread.sleep(5000);
				msgCounter++;
				if (msgCounter % 5 == 0) {
					SendMessage.addToQueue(new IOTUpdateMessage(ERROR + new UID().toString() + AT + SENSOR_NAME, ERROR_MSG, ERROT_REPEAT, SENDING_DELAY));
				}else {
					SendMessage.addToQueue(new IOTUpdateMessage(UPDATE + new UID().toString() + AT + SENSOR_NAME, UPDATE_MSG, UPDATE_REPEAT, SENDING_DELAY));
				}			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}

}