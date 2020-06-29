package il.co.ilrd.pingpong.handlers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		if(true == connectionHandler.isRunning) {
			throw new WrongUseOfSelector("Can't add connections - Server started");
		}
		
		if(!usedPortsList.contains(portNumber)) {
			throw new WrongUseOfSelector("Can't add connections - Port Used");
		}
		
		usedPortsList.add(portNumber);
		connectionHandler.addConnection(new TcpConnection(portNumber));
	}

	public void addUdpConnection(int portNumber) throws WrongUseOfSelector, IOException {
		if(true == connectionHandler.isRunning) {
			throw new WrongUseOfSelector("Can't add connections - Server started");
		}
		
		if(!usedPortsList.contains(portNumber)) {
			throw new WrongUseOfSelector("Can't add connections - Port Used");
		}
		
		usedPortsList.add(portNumber);		
		connectionHandler.addConnection(new UdpConnection(portNumber));
	}	
	
	public void addBroadcastConnection(int portNumber) throws WrongUseOfSelector, IOException {
		if(true == connectionHandler.isRunning) {
			throw new WrongUseOfSelector("Can't add connections - Server started");
		}
		
		if(!usedPortsList.contains(portNumber)) {
			throw new WrongUseOfSelector("Can't add connections - Port Used");
		}
		
		usedPortsList.add(portNumber);
		connectionHandler.addConnection(new BroadcastConnection(portNumber));
	}

/******************************************************************************/
/*------------------------- Connection Interface------------------------------*/
/******************************************************************************/

	private interface Connection {
		
		void sendMessage(ClientInfoWrapper clientInfoWrapper, ByteBuffer message) throws IOException;

		public void InitConnection() throws IOException;
		
		public Channel getChannel();
		
		public void answerClient(Channel client, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException;
	}

/******************************************************************************/
/*------------------------TCP Connection--------------------------------------*/
/******************************************************************************/
	
	private class TcpConnection implements Connection {
		private final int PORT;
		private ServerSocketChannel tcpServerChannel; 
		
		private TcpConnection(int port) throws IOException{
			this.PORT = port;
			tcpServerChannel = ServerSocketChannel.open();
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
			tcpServerChannel.configureBlocking(false);
			tcpServerChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
			tcpServerChannel.register(connectionHandler.selector, SelectionKey.OP_ACCEPT);
		}
		@Override
		public Channel getChannel() {
			return tcpServerChannel;
		}
		@Override
		public void answerClient(Channel clientChannel, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException {
			if(-1 == ((SocketChannel)clientChannel).read(messageBuffer)) {
				clientChannel.close();
				System.out.println("client is closed");
			}
			else {
				messageHandler.handleMessage(messageBuffer, 
						new ClientInfoWrapper(this, (SocketChannel) clientChannel));
			}		 
		}
	}

/******************************************************************************/
/*------------------------UDP Connection--------------------------------------*/
/******************************************************************************/
	
	private class UdpConnection implements Connection {
		private final int PORT;
		private DatagramChannel udpServerChannel;
		
		private UdpConnection(int port) throws IOException{
			this.PORT = port;
			udpServerChannel = DatagramChannel.open();
		}

		@Override
		public void InitConnection() throws IOException {
			udpServerChannel.configureBlocking(false);
			udpServerChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(),PORT));
			udpServerChannel.register(connectionHandler.selector, SelectionKey.OP_READ);
		}

		@Override
		public Channel  getChannel() {
			return udpServerChannel;
		}

		@Override
		public void answerClient(Channel serverChannel, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException {
			SocketAddress clientAddress = ((DatagramChannel) serverChannel).receive(messageBuffer);		
			messageHandler.handleMessage(messageBuffer, new ClientInfoWrapper(this, clientAddress));		
		}

		@Override
		public void sendMessage(ClientInfoWrapper clientInfoWrapper, ByteBuffer messageBuffer) throws IOException {
			messageBuffer.flip();
			((DatagramChannel) this.getChannel()).send(messageBuffer, clientInfoWrapper.getUdpClientAddress());
			messageBuffer.clear();	
		}
	}

/******************************************************************************/
/*------------------------BroadCast Connection--------------------------------*/
/******************************************************************************/
	
	private class BroadcastConnection implements Connection {
		private final int PORT;
		private DatagramChannel broadcastServerChannelListener;
		private DatagramChannel ServerChannelSender; 

		private BroadcastConnection(int port) throws IOException{
			this.PORT = port;
		}

		@Override
		public void InitConnection() throws IOException {
			broadcastServerChannelListener = DatagramChannel.open();
			broadcastServerChannelListener.configureBlocking(false);
			broadcastServerChannelListener.socket().bind(
		    					new InetSocketAddress(InetAddress.getByName("127.255.255.255"), PORT));
			broadcastServerChannelListener.register(connectionHandler.selector, SelectionKey.OP_READ);	
			ServerChannelSender = DatagramChannel.open();
			ServerChannelSender.configureBlocking(false);
			ServerChannelSender.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
		}

		@Override
		public Channel getChannel() {
			return broadcastServerChannelListener;
		}
		
		public DatagramChannel getSenderChannel() {
			return ServerChannelSender;
		}

		@Override
		public void answerClient(Channel clientChannel, ByteBuffer messageBuffer) throws ClassNotFoundException, IOException {
			SocketAddress clientAddress = ((DatagramChannel)clientChannel).receive(messageBuffer);		
			messageHandler.handleMessage(messageBuffer, new ClientInfoWrapper(this, clientAddress));						
		}

		@Override
		public void sendMessage(ClientInfoWrapper clientInfoWrapper, ByteBuffer messageBuffer) throws IOException {
			//BroadcastConnection broadcastConnection = (BroadcastConnection) clientInfoWrapper.getConnection();
			messageBuffer.flip();
			(this.getSenderChannel()).send(messageBuffer, clientInfoWrapper.getUdpClientAddress());
			messageBuffer.clear();				
		}
	}

/******************************************************************************/
/*------------------------ConnectionHandler Connection------------------------*/
/******************************************************************************/
	
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
						connectionMap.get(clientChannel).answerClient(clientChannel, messageBuffer);						
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
	}

/******************************************************************************/
/*------------------------PingPongProtocol------------------------------------*/
/******************************************************************************/
	
	private class PingPongProtocol implements Protocol{
		private static final String PING = "ping"; 
		private static final String PONG = "pong";
		private final PingPongMessage pingMessage = new PingPongMessage(0, "ping");
		private final PingPongMessage pongMessage = new PingPongMessage(0, "pong");
		

		private void SendMessageToClient(ByteBuffer messageBuffer, ClientInfoWrapper clientInfoWrapper) throws IOException {
			clientInfoWrapper.getConnection().sendMessage(clientInfoWrapper, messageBuffer);							
		}


		private void FillBufferWithReturnMessage(Message<?, ?> message, ByteBuffer messageBuffer) throws IOException {
			String inputmessage = (String) message.getKey();
			messageBuffer.clear();
			if(inputmessage.equals(PING)) {
				messageBuffer.put(Message.toByteArray(pongMessage));
			} else if(inputmessage.equals(PONG)) {
				messageBuffer.put(Message.toByteArray(pingMessage));
			}		
		}


		@Override
		public void handleMessage(ClientInfoWrapper clientInfoWrapper, Message<?, ?> message, ByteBuffer messageBuffer)
				throws IOException {

			FillBufferWithReturnMessage(message, messageBuffer);
			SendMessageToClient(messageBuffer, clientInfoWrapper);
			
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
			addProtocol(new PingPongProtocol());
		}
	
		private void handleMessage(ByteBuffer messageBuffer, ClientInfoWrapper clientInfoWrapper) throws IOException, ClassNotFoundException {
			messageBuffer.clear();
			byte[] byteArr = messageBuffer.array();
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
		private SocketAddress udpClientAddress;
		private Connection connection;
		
		public ClientInfoWrapper(Connection connection, SocketChannel tcpClientChannel) {
			this.tcpClientChannel = tcpClientChannel;
			this.connection = connection;
		}
		
		public ClientInfoWrapper(Connection connection, SocketAddress udpClientAddress) {
			this.udpClientAddress = udpClientAddress;
			this.connection = connection;
		}
		
		private SocketChannel getTcpClientChannel() {
			return tcpClientChannel;
		}
		
		private SocketAddress getUdpClientAddress() {
			return udpClientAddress;
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