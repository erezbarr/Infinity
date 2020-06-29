package il.co.ilrd.raspi_clients.heart.monitor.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


import il.co.ilrd.raspi_clients.IOTUpdateMessage;
import il.co.ilrd.raspi_clients.MessagingUtils;


public class SendMessage implements Runnable {
	private IOTUpdateMessage msg;
	
	@Override
	public void run() {
		while(HeartMonitorIOT.isRunning) {
			try {
				HeartMonitorIOT.semNewUpdate.drainPermits();
				HeartMonitorIOT.semQueueMsgs.acquire();
				msg = HeartMonitorIOT.queue.poll();
				
				while(HeartMonitorIOT.semNewUpdate.tryAcquire(msg.getTimeToSend() - System.currentTimeMillis(), TimeUnit.MILLISECONDS)) {
					HeartMonitorIOT.queue.add(msg);
					msg = HeartMonitorIOT.queue.poll();
				}
				
				if (msg.getRetransmissionCounter() > 0) {
					sendToServer(msg.getMessage());
					msg.setTimeSent(System.currentTimeMillis());
					if (msg.decreaseCounter() > 0) {
						MessagingUtils.TurnLedFlicker();
						msg.updateTimeToSend(HeartMonitorIOT.RTT * 3);
						HeartMonitorIOT.queue.add(msg);
					}
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void sendToServer(String value) {
		try {
			MessagingUtils.TurnLed();
			HeartMonitorIOT.clientSocket.write(
			ByteBuffer.wrap((MessagingUtils.prepareMessage(value, HeartMonitorIOT.DB_NAME, HeartMonitorIOT.serialNumber).getBytes())));
		} catch (IOException e) {
			HeartMonitorIOT.close();
		}	
	}

	public static void addToQueue(IOTUpdateMessage newIOTUpdateMessage) {
		HeartMonitorIOT.queue.add(newIOTUpdateMessage);
		HeartMonitorIOT.idToIOTMap.put(newIOTUpdateMessage.getID(), newIOTUpdateMessage);
		HeartMonitorIOT.semQueueMsgs.release();
		HeartMonitorIOT.semNewUpdate.release();
	}
}