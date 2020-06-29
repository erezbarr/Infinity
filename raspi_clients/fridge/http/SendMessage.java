package il.co.ilrd.raspi_clients.fridge.http;

import java.io.IOException;
import java.io.OutputStream;


import il.co.ilrd.http_message.*;
import il.co.ilrd.raspi_clients.IOTUpdateMessage;
import il.co.ilrd.raspi_clients.MessagingUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SendMessage implements Runnable {
	private Map<String, String> headersMap = new HashMap<>();
	private IOTUpdateMessage msg;

	@Override
	public void run() {
		headersMap.put("Content-Type", "application/json");
		try {
		OutputStream outputStream = FridgeIOT.socket.getOutputStream();
		while(FridgeIOT.isRunning) {
				FridgeIOT.semNewUpdate.drainPermits();
				FridgeIOT.semQueueMsgs.acquire();
				msg = FridgeIOT.queue.poll();
				
				while(FridgeIOT.semNewUpdate.tryAcquire(msg.getTimeToSend() - System.currentTimeMillis(), TimeUnit.MILLISECONDS)) {
					FridgeIOT.queue.add(msg);
					msg = FridgeIOT.queue.poll();
				}
				
				if (msg.getRetransmissionCounter() > 0) {
					sendMessages(msg.getMessage(), outputStream);
					msg.setTimeSent(System.currentTimeMillis());
					if (msg.decreaseCounter() > 0) {
						MessagingUtils.TurnLedFlicker();
						msg.updateTimeToSend(FridgeIOT.RTT * 3);
						FridgeIOT.queue.add(msg);
					}
				}
				
			}
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessages(String message, OutputStream outputStream) throws IOException {
		MessagingUtils.TurnLed();
		String rawData = MessagingUtils.prepareMessage(message, FridgeIOT.DB_NAME, FridgeIOT.serialNumber);
		headersMap.put("Content-Length",Integer.toString(rawData.length()));			
		String httpMessage = HttpBuilder.createHttpRequestMessage(
				HttpMethod.POST, 
				HttpVersion.HTTP_1_1,
				"/", 
				headersMap, 
				rawData);
		outputStream.write(httpMessage.getBytes());
		outputStream.flush();
	}

	public static void addToQueue(IOTUpdateMessage newIOTUpdateMessage) {
		FridgeIOT.queue.add(newIOTUpdateMessage);
		FridgeIOT.idToIOTMap.put(newIOTUpdateMessage.getID(), newIOTUpdateMessage);
		FridgeIOT.semQueueMsgs.release();
		FridgeIOT.semNewUpdate.release();
	}
}