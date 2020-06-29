package il.co.ilrd.chatserver;

import java.nio.ByteBuffer;

public class ServerMessage {
	ProtocolType chatServer;
	ChatServerMessage chatServerMessage;
	
	public ServerMessage(ProtocolType chatServer, ChatServerMessage chatServerMessage) {
		this.chatServer = chatServer;
		this.chatServerMessage = chatServerMessage;
	}

	public static ByteBuffer toByteArray(ServerMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ServerMessage toObject(byte[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Enum<ChatProtocolKeys> getKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
