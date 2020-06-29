package il.co.ilrd.raspi_clients.weather_station;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import il.co.ilrd.raspi_clients.IOTUpdateMessage;
import il.co.ilrd.raspi_clients.MessagingUtils;

public class SendMessage implements Runnable {
	private IOTUpdateMessage msg;

	@Override
	public void run() {
		
		while(WeatherStationIOT.isRunning) {
			try {
				WeatherStationIOT.semNewUpdate.drainPermits();
				WeatherStationIOT.semQueueMsgs.acquire();
				msg = WeatherStationIOT.queue.poll();
				
				while(WeatherStationIOT.semNewUpdate.tryAcquire(msg.getTimeToSend() - System.currentTimeMillis(), TimeUnit.MILLISECONDS)) {
					WeatherStationIOT.queue.add(msg);
					msg = WeatherStationIOT.queue.poll();
				}
				
				if (msg.getRetransmissionCounter() > 0) {
					sendToServer(msg.getMessage());
					msg.setTimeSent(System.currentTimeMillis());
					
					if (msg.decreaseCounter() > 0) {
						MessagingUtils.TurnLedFlicker();
						msg.updateTimeToSend(WeatherStationIOT.RTT * 3);
						WeatherStationIOT.queue.add(msg);
					}
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void sendToServer(String rawData) {
        DatagramPacket packet;
		try {
			MessagingUtils.TurnLed();
			String msg = MessagingUtils.prepareMessage(rawData, WeatherStationIOT.DB_NAME, WeatherStationIOT.serialNumber);
			packet = new DatagramPacket(msg.getBytes(), 
					msg.getBytes().length, InetAddress.getByName(WeatherStationIOT.IP), WeatherStationIOT.PORT);
			WeatherStationIOT.socket.send(packet);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addToQueue(IOTUpdateMessage newIOTUpdateMessage) {
		WeatherStationIOT.semQueueMsgs.release();
		WeatherStationIOT.semNewUpdate.release();
		WeatherStationIOT.queue.add(newIOTUpdateMessage);
		WeatherStationIOT.idToIOTMap.put(newIOTUpdateMessage.getID(), newIOTUpdateMessage);
	}
}