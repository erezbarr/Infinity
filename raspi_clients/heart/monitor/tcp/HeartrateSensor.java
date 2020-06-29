package il.co.ilrd.raspi_clients.heart.monitor.tcp;

import java.rmi.server.UID;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;

public class HeartrateSensor implements Runnable {
	static int msgCounter = 0;
	private static final String SENSOR_NAME = "heartrate";
	private static final String ERROR = "ERROR ";
	private static final String UPDATE = "UPDATE ";
	private static final String AT = "@";
	private static final String ERROR_MSG = "heart rate is 200 ";
	private static final String UPDATE_MSG = "heart rate is 100 ";
	private static final int ERROT_REPEAT = 3;
	private static final int UPDATE_REPEAT = 1;
	private static final int SENDING_DELAY = 1;

	@Override
	public void run() {
		while(HeartMonitorIOT.isRunning) {
			try {
				Thread.sleep(5000);
				msgCounter++;
				if (msgCounter % 5 == 0) {
					SendMessage.addToQueue(new IOTUpdateMessage(ERROR + new UID().toString() + AT + SENSOR_NAME, ERROR_MSG, ERROT_REPEAT, SENDING_DELAY));
				}else {
					SendMessage.addToQueue(new IOTUpdateMessage(UPDATE + new UID().toString() + AT + SENSOR_NAME, UPDATE_MSG, UPDATE_REPEAT, SENDING_DELAY));
				}			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}		
	}
}