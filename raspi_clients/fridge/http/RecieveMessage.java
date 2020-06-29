package il.co.ilrd.raspi_clients.fridge.http;

import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import il.co.ilrd.http_message.HttpParser;
import il.co.ilrd.raspi_clients.*;

public class RecieveMessage implements Runnable {
	private final static String COMMAND_TYPE = "Succsess";
	GsonBuilder builder = new GsonBuilder();
	Gson gson = builder.create();
	JsonReader reader;
	
	@Override
	public void run() {
		try { 
		InputStream inputStream = FridgeIOT.socket.getInputStream();
		IOTUpdateMessage currentMsg = null;

		while(FridgeIOT.isRunning) {
			byte[] byteArr = new byte[FridgeIOT.BUFFER_SIZE];
				inputStream.read(byteArr);
				String responseMessage = new String(byteArr);
				HttpParser httpPareser = new HttpParser(responseMessage);
				String MessageID = getMessageID(ByteBuffer.wrap(httpPareser.getBody().getBody().getBytes()));
				MessagingUtils.TurnLedLong();
				if (MessageID != null) {
					FridgeIOT.semQueueMsgs.tryAcquire();
					currentMsg = FridgeIOT.idToIOTMap.get(MessageID);
					if (null ==  currentMsg) {
						continue;
					}
					FridgeIOT.RTT = MessagingUtils.calculateRTT(FridgeIOT.RTT, System.currentTimeMillis() - currentMsg.getTimeSent());
					FridgeIOT.queue.remove(FridgeIOT.idToIOTMap.get(MessageID));
					FridgeIOT.idToIOTMap.remove(MessageID);

				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println("Connection Closed!");
			FridgeIOT.close();
		}
	}

	private String getMessageID(ByteBuffer buffer) throws UnsupportedEncodingException {
		reader = new JsonReader(new StringReader(new String(buffer.array(), "UTF-8")));
		System.out.println("GET KEY: " + new String(buffer.array(), "UTF-8"));
		reader.setLenient(true);
		JsonObject j = gson.fromJson(reader, JsonObject.class);
		String[] splittedResponse = j.get(COMMAND_TYPE).getAsString().split("\\$");
		return splittedResponse[1].replace("'", "");			
	}
}