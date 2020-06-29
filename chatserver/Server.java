package il.co.ilrd.chatserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server implements Runnable{
	private MessageHandler messageHandler = new MessageHandler();
	private ConnectionHandler connectionHandler;
	private List<Integer> usedPortsList = new ArrayList<>();

	protected Server() throws IOException{
		connectionHandler = new ConnectionHandler();
	}

	public void run() {
		try {
			connectionHandler.startConnections();
		} catch (IOException | WrongUseOfSelector e) {
			e.printStackTrace();
		}
		new Thread(connectionHandler).start();
	}

	public void stopServer() throws IOException {
		connectionHandler.stopConnections();
	}

	public void addTcpConnection(int portNumber) throws IOException, WrongUseOfSelector {
		CheckPortAndServerDuplications(portNumber);
		usedPortsList.add(portNumber);
		connectionHandler.addConnection(new TcpConnection(portNumber));
	}
	
	private void CheckPortAndServerDuplications(int portNumber) throws WrongUseOfSelector {
		if(true == connectionHandler.isRunning) {
			throw new WrongUseOfSelector("Can't add connections - Server started");
		}
		
		if(usedPortsList.contains(portNumber)) {
			throw new WrongUseOfSelector("Can't add connections - Port Used");
		}
	}

/******************************************************************************/
/*------------------------- Connection Interface------------------------------*/
/******************************************************************************/

	private interface Connection {
		
		void sendMessage(ClientInfoWrapper clientInfoWrapper, ByteBuffer message) throws IOException;

		public void InitConnection() throws IOException;
		
		public Channel getChannel();
		
		public void answerClient(Map<Channel, Connection>  clientMap, Channel SenderClient, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException;
	}

/******************************************************************************/
/*------------------------TCP Connection--------------------------------------*/
/******************************************************************************/
	
	private class TcpConnection implements Connection {
		private final int PORT;
		private ServerSocketChannel tcpServerChannel; 
		
		private TcpConnection(int port) throws IOException{
			this.PORT = port;
		}
		@Override
		public void sendMessage(ClientInfoWrapper clientInfoWrapper, ByteBuffer messageBuffer) throws IOException {
			messageBuffer.flip();
			while(messageBuffer.hasRemaining()) {
				clientInfoWrapper.getTcpClientChannel().write(messageBuffer);				
			}
			messageBuffer.clear();
		}
		@Override
		public void InitConnection() throws IOException {
			tcpServerChannel = ServerSocketChannel.open();
			tcpServerChannel.configureBlocking(false);
			tcpServerChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
			tcpServerChannel.register(connectionHandler.selector, SelectionKey.OP_ACCEPT);
		}
		@Override
		public Channel getChannel() {
			return tcpServerChannel;
		}
		@Override
		public void answerClient(Map<Channel, Connection>  clientMap, Channel SenderClient, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException {
			if (clientMap.containsKey(SenderClient)) {
				if(-1 == ((SocketChannel)SenderClient).read(messageBuffer)) {
					SenderClient.close();
					System.out.println("client is closed");
					clientMap.remove(SenderClient);
				}
				else {
						messageHandler.handleMessage(messageBuffer, 
								new ClientInfoWrapper(this, (SocketChannel) SenderClient)); 
				}
			} else {
				String errorString = "client is unregistered";
				messageBuffer.clear();
				messageBuffer.put(errorString.getBytes());
				messageBuffer.flip();
				while(messageBuffer.hasRemaining()) {
					((SocketChannel)SenderClient).write(messageBuffer);				
				}
				messageBuffer.clear();
			}
		}
	}
	
	private class ConnectionHandler implements Runnable {
		private Selector selector;
		private List<Connection> connectionList  = new ArrayList<>();
		private Map<Channel, Connection> connectionMap = new HashMap<>();
		private static final int BYTE_CAPACITY = 1024;
		private ByteBuffer messageBuffer = ByteBuffer.allocate(BYTE_CAPACITY);;
		private boolean isRunning = false;
		private final long SELECT_TIMEOUT = 5000;

		private void startConnections() throws IOException, WrongUseOfSelector {
			selector = Selector.open();
			if(connectionList.isEmpty()) {
				throw new WrongUseOfSelector("No Connections Present");
			}
			for(Connection connectionRunner : connectionList) {
				connectionRunner.InitConnection();
				connectionMap.put(connectionRunner.getChannel(), connectionRunner);
				System.out.println("I'm Here");
			}
		}
		
		private void stopConnections() throws IOException {
			isRunning = false;
			selector.close();
		}

		private void addConnection(Connection connection) {
			if (!connectionList.contains(connection)) {
				connectionList.add(connection);
			}
		}

		private void removeConnection(Connection connection) {
			connectionList.remove(connection);
		}

		@Override
		public void run() {
			isRunning = true;
			try {
				while(true) {
				if(0 == selector.select(SELECT_TIMEOUT)) {
					System.out.println("server running");
					continue;
				}
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while(iter.hasNext()) {
					SelectionKey key = iter.next();
					if(key.isAcceptable()) {
					//	System.out.println("Acceptable");
						registerClientSoket(key);
					}
					if(key.isReadable()) {	
					//	System.out.println("Readable");
						Channel clientChannel =  key.channel();
						connectionMap.get(clientChannel).answerClient(connectionMap, clientChannel, messageBuffer);						
						}
					}
					iter.remove();
				}

				} catch(Exception e) {
					if(isRunning) {
						throw new RuntimeException(e);
					}
				}
		}

		private void registerClientSoket(SelectionKey key) throws IOException {
			ServerSocketChannel serverChannel = 
						(ServerSocketChannel)key.channel();
			SocketChannel client = serverChannel.accept();
			client.configureBlocking(false);
			connectionMap.put(client, connectionMap.get(serverChannel));
			client.register(selector, SelectionKey.OP_READ);
		}
	}
/******************************************************************************/
/*------------------------Protocol--------------------------------------------*/
/******************************************************************************/
	
	private interface Protocol{
		public void handleMessage(ClientInfoWrapper clientInfoWrapper, Message<?, ?> message,
												ByteBuffer messageBuffer) throws IOException;

		ProtocolType getKeyProtocol();
	}

	/**********************************************
	 * chat Protocol
	 **********************************************/
	private class ChatProtocol implements Protocol {		
		private final ProtocolType PROTOCOL_KEY = ProtocolType.CHAT_SERVER;
		private LinkedList<ChatUserInfo> usersList = new LinkedList<>();
		private ChatUserInfo senderInfo;
		private String senderName;		
		private String chatRoomMessage;
		private String userLeft;		
		private ByteBuffer sendingBuffer;
		private ByteBuffer responseBuffer;
		private ByteBuffer setBuffer = ByteBuffer.allocate(BUFFER_SIZE);;
		private ChatServerMessage receivedMessage = new ChatServerMessage(null, null);
		private ServerMessage messageToSend;
		
		private static final String ADDED_USER_RETURN_MESSAGE = "you just joined the chat";
		private static final String ADDED_USER_ALREADY_REGISTERED = "you already registered!";
		private static final String ADDED_USER_ROOM_MESSAGE = " just joined the chat";
		private static final String USER_NOT_REGISTERED = "you are not registered!";
		private static final String INVALID_KEY_MESSAGE = "invalid key";
		private static final String INVALID_NAME = "invalid name";
		private static final String USER_LEAVE_ROOM_MESSAGE = " has left the chat";
		private static final int BUFFER_SIZE = 2048;
		
		
		
		@Override
		public ProtocolType getKeyProtocol() {
			return PROTOCOL_KEY;
		}
		
		@Override
		public void handleMessage(ClientInfoWrapper clientInfoWrapper, Message<?, ?> message,
								  ByteBuffer messageBuffer) throws IOException {
			receivedMessage = (ChatServerMessage) message;
			ChatProtocolKeys receivedMessageKey = receivedMessage.getKey();
			//System.out.println("received Key: " + receivedMessageKey);
			
			switch (receivedMessageKey) {
			case REGISTRATION_REQUEST:
				registerNewClient(receivedMessage, clientInfoWrapper);
				break;
			case MESSAGE:
				handleChatRoomMessage(receivedMessage, clientInfoWrapper);
				break;
			case REMOVE_REQUEST:
				removeClient(clientInfoWrapper);
				break;
			default:
				returnMessageToSender(clientInfoWrapper, ChatProtocolKeys.ERROR_MESSAGE, INVALID_KEY_MESSAGE);
			}		
		}		

		private void registerNewClient(ChatServerMessage registrationMessage, ClientInfoWrapper clientInfo) throws IOException {
			String newUserName = registrationMessage.getData();
			if(isClientRegistered(clientInfo)) {
				returnMessageToSender(clientInfo, ChatProtocolKeys.ERROR_MESSAGE, ADDED_USER_ALREADY_REGISTERED);
			} 
			else if(!isNameValid(newUserName)) {
				returnMessageToSender(clientInfo, ChatProtocolKeys.ERROR_MESSAGE, INVALID_NAME);
			} else {
				addChatUser(clientInfo, registrationMessage);
				returnMessageToSender(clientInfo, ChatProtocolKeys.REGISTRATION_ACK, ADDED_USER_RETURN_MESSAGE);
				sendMessagesToChatRoom(findChatClient(clientInfo), 
									   ChatProtocolKeys.NEW_CLIENT_REGISTRATION, 
									   newUserName + ADDED_USER_ROOM_MESSAGE);
			}
		}

		private void handleChatRoomMessage(ChatServerMessage innerMessage, ClientInfoWrapper clientInfo) throws IOException {
			if(isClientRegistered(clientInfo)) {
				senderInfo = findChatClient(clientInfo);
				senderName = senderInfo.getName();
				chatRoomMessage = senderName + ": " + innerMessage.getData();
				sendMessagesToChatRoom(senderInfo, ChatProtocolKeys.BROADCAST_MESSAGE, chatRoomMessage );
			} else {				
				returnMessageToSender(clientInfo, ChatProtocolKeys.ERROR_MESSAGE, USER_NOT_REGISTERED);
			}							
		}

		private void removeClient(ClientInfoWrapper clientInfo) throws IOException {
			if(isClientRegistered(clientInfo)) {
				senderInfo = findChatClient(clientInfo);
				userLeft = senderInfo.getName();	
				sendMessagesToChatRoom(senderInfo, ChatProtocolKeys.REMOVE_REQUEST, userLeft + USER_LEAVE_ROOM_MESSAGE);
				usersList.remove(senderInfo);
			} else {
				returnMessageToSender(clientInfo, ChatProtocolKeys.ERROR_MESSAGE, USER_NOT_REGISTERED);
				
			}
		}
		
		private void returnMessageToSender(ClientInfoWrapper clientInfo, ChatProtocolKeys key, String dataToSend) throws IOException {
			messageToSend = new ServerMessage(PROTOCOL_KEY, new ChatServerMessage(key, dataToSend));			
			System.out.println("server send to one user: " + messageToSend);
			responseBuffer = setBuffer(messageToSend);			
			Connection connection = connectionHandler.connectionMap.get(clientInfo.getTcpClientChannel());
			if(null != connection) {
				connection.sendMessage(clientInfo, responseBuffer);				
			}
		}
		
		private void sendMessagesToChatRoom(ChatUserInfo senderUserInfo, ChatProtocolKeys key, String dataToSend) throws IOException {
			ServerMessage messageToGroup = new ServerMessage(PROTOCOL_KEY, new ChatServerMessage(key, dataToSend));
			sendingBuffer = setBuffer(messageToGroup);
			senderName = senderUserInfo.getName();
			
			System.out.println("server send to group: " + messageToGroup);			
			for(ChatUserInfo chatUserInfo : usersList) {
				String userName = chatUserInfo.getName();		
				Connection connection = connectionHandler.connectionMap.get(chatUserInfo.getClientInfo().getTcpClientChannel());
				if(null != connection && !senderName.equals(userName)) {
					connection.sendMessage(chatUserInfo.getClientInfo(), sendingBuffer);
				} else if(null == connection) {
					removeClient(chatUserInfo.getClientInfo());					
				}
			}			
		}

		private ByteBuffer setBuffer(ServerMessage messageToGroup) throws IOException {			
			setBuffer.clear();
			setBuffer.put(ServerMessage.toByteArray(messageToGroup));
			setBuffer.flip();
			return setBuffer;
		}
		
		private void addChatUser(ClientInfoWrapper clientInfo, ChatServerMessage innerMessage) {			
			System.out.println("adding chat user");
			String newUserName = innerMessage.getData();
			usersList.add(new ChatUserInfo(newUserName, clientInfo));				
		}
		
		private boolean isNameValid(String newName) {
			for (ChatUserInfo chatUserInfo : usersList) {
				if (chatUserInfo.getName().equals(newName)){
					return false;
				}
			}			
			return true;			
		}
		
		private ChatUserInfo findChatClient(ClientInfoWrapper info) {
			for (ChatUserInfo chatUserInfo : usersList) {
				if (chatUserInfo.getClientInfo().getTcpClientChannel().equals(info.getTcpClientChannel())){
					return chatUserInfo;
				}
			}
			return null;
		}
		
		private boolean isClientRegistered(ClientInfoWrapper clientInfo) {
			if(null == findChatClient(clientInfo)) {
				return false;				
			}			
			return true;
		}

		private class ChatUserInfo {
			private String name;
			private ClientInfoWrapper clientInfo;
			
			public ChatUserInfo(String name, ClientInfoWrapper clientInfo) {
				this.name = name;
				this.clientInfo = clientInfo;
			}

			public String getName() {
				return name;
			}

			public ClientInfoWrapper getClientInfo() {
				return clientInfo;
			}			
		}
	}

/******************************************************************************/
/*------------------------MessageHandler--------------------------------------*/
/******************************************************************************/
		
	private class MessageHandler {
		private Map<Integer, Protocol> protocolMap;
		private int protocolKey = 0;
		
		private MessageHandler(){
			protocolMap = new HashMap<>();
			addProtocol(new ChatProtocol());
		}
	
		private void handleMessage(ByteBuffer messageBuffer, ClientInfoWrapper clientInfoWrapper) throws IOException, ClassNotFoundException {
			messageBuffer.clear();
			byte[] byteArr = messageBuffer.array();
			@SuppressWarnings("unchecked")
			Message<Integer, Message<?, ?>> message = (Message<Integer, Message<?, ?>>) Message.toObject(byteArr); 	
			protocolMap.get(message.getKey()).handleMessage(clientInfoWrapper, message.getData(), messageBuffer);
		}

		private void addProtocol(Protocol protocol) {
			protocolMap.put(protocolKey++, protocol);
		}

		private void removeProtocol(Protocol protocol) {

		}
	}
	
/******************************************************************************/	
	
	private class ClientInfoWrapper {
		private SocketChannel tcpClientChannel;
		private Connection connection;
		
		public ClientInfoWrapper(Connection connection, SocketChannel tcpClientChannel) {
			this.tcpClientChannel = tcpClientChannel;
			this.connection = connection;
		}
		
		private SocketChannel getTcpClientChannel() {
			return tcpClientChannel;
		}
		
		private Connection getConnection() {
			return connection;
		}
	}
	
/******************************************************************************/
	
	public class WrongUseOfSelector extends Exception { 
	private static final long serialVersionUID = 1L;

		public WrongUseOfSelector(String errorMessage) {
	        super(errorMessage);
	    }
	}

}