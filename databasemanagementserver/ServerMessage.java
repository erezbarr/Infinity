package il.co.ilrd.databasemanagementserver;

import java.io.IOException;
import java.io.Serializable;
import il.co.ilrd.chatserver.Message;


public class ServerMessage implements Message<ProtocolType, Message<?, ?>> ,Serializable{
		
	private static final long serialVersionUID = 55555;
	private ProtocolType key; 
	private Message<?, ?> data;
		
	public ServerMessage(ProtocolType key, Message<?, ?> data) {
		this.key = key;
		this.data = data;
	}

	@Override
	public ProtocolType getKey() {
		return key;
	}


	public void setData(Message<?, ?> data) {
		this.data = data;
	}

	@Override
	public Message<?, ?> getData() {
		return data;
	}

	public static ServerMessage toObject(byte[] array) throws ClassNotFoundException, IOException {	
		return (ServerMessage) ByteUtil.toObject(array);
	}

	public static byte[] toByteArray(ServerMessage messageToGroup) throws IOException {
		return ByteUtil.toByteArray(messageToGroup);
	}
}