package il.co.ilrd.gatewayserver;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import il.co.ilrd.http_message.HttpBuilder;
import il.co.ilrd.http_message.HttpParser;
import il.co.ilrd.http_message.HttpParser.HttpFormatException;
import il.co.ilrd.observer.Callback;
import il.co.ilrd.observer.Dispatcher;
import il.co.ilrd.http_message.HttpStatusCode;

public class GatewayServer implements Runnable{
	private ThreadPoolExecutor threadPool;
	private static CMDFactory<FactoryCommand, CommandKey, String> cmdFactory = new CMDFactory<>();
	private ConnectionsHandler connectionHandler =  new ConnectionsHandler();
	private MessageHandler messageHandler = new MessageHandler();
	FactoryCommandLoader factoryCommandLoader = new FactoryCommandLoader();
	private boolean isRunning = true;
	private static final String jarPath = "/home/student/git/erez-barr/fs/projects/src/JarForGateway/";

	public void run() {
		try {
			connectionHandler.startConnections();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GatewayServer(int numOfThreads) {
		threadPool = new ThreadPoolExecutor(numOfThreads, 
											Math.max(1,numOfThreads), 
											1, 
											TimeUnit.SECONDS, 
											new LinkedBlockingQueue<Runnable>());		
		initFactory();
	}
	
	public GatewayServer() {
		this(Runtime.getRuntime().availableProcessors());
	}
	
	public void addHttpServer(ServerPort port) throws Exception {
		checkPort(port, ServerPort.HTTP_SERVER_PORT);
//		ServerConnection httpServer = new LowLevelHttpServer();
		ServerConnection httpServer = new HighLevelHttpServer();
		connectionHandler.addServer(httpServer);
		
	}
	
	public void addTcpServer(ServerPort port) throws Exception {			
		checkPort(port, ServerPort.TCP_SERVER_PORT);
		connectionHandler.addServer(new TcpServer());
	}
	
	public void addUdpServer(ServerPort port) throws Exception {
		checkPort(port, ServerPort.UDP_SERVER_PORT);
		connectionHandler.addServer(new UdpServer());
	}
	
	
	public void stop() {
		connectionHandler.stopConnections();
		threadPool.shutdown();
		try {
			factoryCommandLoader.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNumOfThreads(int numOfThread) {
		threadPool.setCorePoolSize(numOfThread);
	}

	private void checkPort(ServerPort recivePort, 
						   ServerPort actualPort)
								   throws Exception {
		final String EXCEPTION_MSG = "the port is not the correct port";

		if(!recivePort.equals(actualPort)) {
			throw new Exception(EXCEPTION_MSG);
		}
	}
	
	private void initFactory() {
			factoryCommandLoader.start();
	}
		
/****************************************************************
 * Connection Handler
 ****************************************************************/
	
	private class ConnectionsHandler implements Runnable {
		private List<ServerConnection> serverconnectionsMAP = new LinkedList<>();
		private Map<Channel, ServerConnection> socketconnectionsMAP = new HashMap<>();
		protected Map<SocketChannel, ClientInfo> socketClientInfoMAP = new HashMap<>();
		private ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		private boolean toContinueRun = false;
		private boolean isResourceClosed = false;
		private Selector selector = null;
		private HttpServer sunHttpServer = null;
		private final static int BUFFER_SIZE = 8192;

		
		public void addServer(ServerConnection connection) {
				Objects.requireNonNull(connection);
				serverconnectionsMAP.add(connection);
		}

		public void startConnections() throws Exception {
			if(isRunning) {
				throw new Exception("the server start allready.");
			}
			
			registerConnectionsToSelector();
			new Thread(this).start();
			
			if(null != sunHttpServer) {
				sunHttpServer.start();				
			}
		}

		private void registerConnectionsToSelector() throws IOException {
			selector = Selector.open();
			
			for (ServerConnection connection : serverconnectionsMAP) {
				connection.initServerConnection();
				socketconnectionsMAP.put(connection.getChannel(), connection);
			}
		}
		
		private void stopConnections() {
			toContinueRun = false;
			final int DELAY = 0;
			
			if(!isResourceClosed) {
				closeResource();
			}
			
			if(null != sunHttpServer) {
				sunHttpServer.stop(DELAY);				
			}
		}
		

		@Override
		public void run() {
			try {
				isRunning = true;
				toContinueRun = true;
				runServer();
			} catch (IOException | ClassNotFoundException e) {
				stopConnections();
//				System.out.println("Server- run override function: " + e);
			} 			
		}
		
		private void runServer() throws IOException, ClassNotFoundException {
			final String RUN_MSG = "server is up";
			final int SERVER_TIMEOUT = 10000;
			
			try {
				while (toContinueRun) {
					if(0 == selector.select(SERVER_TIMEOUT)) {
						System.out.println(RUN_MSG);
						continue;
					}
					
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> iter = selectedKeys.iterator();
			
					while (iter.hasNext()) {
						SelectionKey key = iter.next();
	
						if (key.isValid() && key.isAcceptable()) {
							registerTcpClientToSelector(key);
						}
						else if (key.isValid() && key.isReadable()) {
							Channel currChannel = key.channel();
							socketconnectionsMAP.get(currChannel).handleRequestMessage(currChannel);
						}
						
						iter.remove();
					}
				}
			}catch (Exception e) {
				if(toContinueRun) {
					stopConnections();
					System.out.println("runServer: " + e.getMessage());
				}
			}
		}
		
		private void registerTcpClientToSelector(SelectionKey key) 
				throws IOException {
			ServerSocketChannel tcpserverSocket = 
			(ServerSocketChannel) key.channel();
			SocketChannel clientTcp = tcpserverSocket.accept();
			clientTcp.configureBlocking(false);
			clientTcp.register(selector, SelectionKey.OP_READ);
			ServerConnection connection = socketconnectionsMAP.get(tcpserverSocket);
			socketconnectionsMAP.put(clientTcp, connection);
			socketClientInfoMAP.put(clientTcp, new TcpClientInfo(clientTcp, connection));
		}
		
		private void closeResource() {			
			try {
				Set<SelectionKey> selectedKeys = selector.keys();
				
				for (SelectionKey Key : selectedKeys) {
					Key.channel().close();
				}
				
				selector.close();
				isResourceClosed   = true;
			} catch (IOException e) {
				System.out.println("close resource: " + e.getMessage());
			}
		}
		
		private void closeAndRemoveClient(SocketChannel client) throws IOException {
			client.close();
			connectionHandler.socketconnectionsMAP.remove(client, this);
		}

		public ByteBuffer getMsgBuffer() {
			return messageBuffer;
		}
		
	}
	
	/****************************************************************
	 * Server Connection
	 *****************************************************************/
	
	private interface ServerConnection {
		public void initServerConnection() throws IOException;
		public void handleRequestMessage(Channel channel) throws IOException;
		public void sendResponse(String message, ClientInfo clientInfo) throws IOException;
		public Channel getChannel();
	}
	
	
	/****************************************************************
	 * TCP
	 *****************************************************************/
	
	private class TcpServer implements ServerConnection {
		private ServerSocketChannel tcpServerSocket;

		@Override
		public void initServerConnection() throws IOException  {
			tcpServerSocket = ServerSocketChannel.open();
			tcpServerSocket.bind(new InetSocketAddress(ServerPort.TCP_SERVER_PORT.getPort()));
			tcpServerSocket.configureBlocking(false);
			tcpServerSocket.register(connectionHandler.selector, SelectionKey.OP_ACCEPT);		
		}

		@Override
		public void handleRequestMessage(Channel channel) throws IOException 
														 {
			SocketChannel client = (SocketChannel) channel;
			ByteBuffer messageBuffer = connectionHandler.getMsgBuffer();
			messageBuffer.clear();
			
			if(-1 == client.read(messageBuffer)) {
				connectionHandler.closeAndRemoveClient(client);					
			}
			else {
				ClientInfo clientInfo = connectionHandler.socketClientInfoMAP.get(client);
				clientInfo.getConnection().sendResponse(new String(messageBuffer.array()), 
														clientInfo);
			}
		}

		@Override
		public void sendResponse(String message, ClientInfo clientInfo) throws IOException {
			SocketChannel client = (SocketChannel) clientInfo.getChannel();
			ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());

			while(messageBuffer.hasRemaining()) {
				client.write(messageBuffer);
			}
		}

		@Override
		public Channel getChannel() {
			return tcpServerSocket;
		}

	}
	
	
	/****************************************************************
	 * UDP
	 *****************************************************************/
		
	private class UdpServer implements ServerConnection {
		private DatagramChannel udpServerDatagram;

		@Override
		public void initServerConnection() throws IOException {
			udpServerDatagram = DatagramChannel.open();
			udpServerDatagram.socket().bind(new InetSocketAddress(ServerPort.UDP_SERVER_PORT.getPort()));
			udpServerDatagram.configureBlocking(false);
			udpServerDatagram.register(connectionHandler.selector, SelectionKey.OP_READ);	
		}

		public void handleRequestMessage(Channel channel) throws IOException {
			DatagramChannel client = (DatagramChannel)channel;
			ByteBuffer messageBuffer = connectionHandler.getMsgBuffer();
			SocketAddress clientAddress = client.receive(messageBuffer);
			ClientInfo clientInfo = new UdpClientInfo(client, clientAddress, this);
			
			if(null != clientAddress) {
				clientInfo.getConnection().sendResponse(new String(messageBuffer.array()), 
														clientInfo);
			}
		}

		@Override
		public void sendResponse(String message, ClientInfo clientInfo) throws IOException {
			DatagramChannel client = (DatagramChannel)clientInfo.getChannel();
			ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());

			client.send(messageBuffer, clientInfo.getclientAddress());
			messageBuffer.clear();			
		}
		
		@Override
		public Channel getChannel() {
			return udpServerDatagram;
		}
	}
	
	/****************************************************************
	 * LowLevel HTTP
	 *****************************************************************/
		
	private class LowLevelHttpServer implements ServerConnection {
		private ServerSocketChannel tcpServerSocket;
		private HttpParser parser;
		private Map<String, String> headersResponse = new HashMap<>();
		private static final String CONTENT_LENGTH = "Content-Length";
		private static final String CONTENT_TYPE = "Content-Type";
		private static final String APP_JSON = "application/json";
				
		public LowLevelHttpServer() {
			initHeadersResponse();
		}

		private void initHeadersResponse() {
			headersResponse.put(CONTENT_TYPE, APP_JSON);
		}
		
		@Override
		public void initServerConnection() throws IOException  {
			tcpServerSocket = ServerSocketChannel.open();
			tcpServerSocket.bind(new InetSocketAddress(ServerPort.HTTP_SERVER_PORT.getPort()));
			tcpServerSocket.configureBlocking(false);
			tcpServerSocket.register(connectionHandler.selector, SelectionKey.OP_ACCEPT);		
		}

		@Override
		public void handleRequestMessage(Channel channel) throws IOException {
			SocketChannel client = (SocketChannel) channel;
			ByteBuffer messageBuffer = connectionHandler.getMsgBuffer();
			messageBuffer.clear();
			
			if(-1 == client.read(messageBuffer)) {
				connectionHandler.closeAndRemoveClient(client);					
			}
			else {
				try {
				messageBuffer.clear();
				ClientInfo clientInfo = connectionHandler.socketClientInfoMAP.get(client);
				parser = new HttpParser( new String(messageBuffer.array()));
				clientInfo.getConnection().sendResponse(parser.getBody().getBodyString(), clientInfo);
				} catch (IOException | HttpFormatException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void sendResponse(String message, ClientInfo clientInfo) throws IOException {
			SocketChannel client = (SocketChannel) clientInfo.getChannel();
			String httpResponseMsg = createResponseMsg(HttpStatusCode.OK, message);
			ByteBuffer messageBuffer = ByteBuffer.wrap(httpResponseMsg.getBytes());
			
			while(messageBuffer.hasRemaining()) {
				client.write(messageBuffer);
			}
		}

		@Override
		public Channel getChannel() {
			return tcpServerSocket;
		}
		
		private String createResponseMsg(HttpStatusCode statusCode, String bodyResMsg) {
			headersResponse.put(CONTENT_LENGTH, Integer.toString(bodyResMsg.length()));

			return HttpBuilder.createHttpResponseMessage(parser.getStartLine().getHttpVersion(), 
														 statusCode,
														 headersResponse, 
														 bodyResMsg);
		}
	}

	/****************************************************************
	 * High Level HTPP
	 *****************************************************************/
	
	private class HighLevelHttpServer implements ServerConnection {
		private HttpExchange exchangeMsg;
		
		private final static String CONTENT_TYPE_HEADER = "Content-Type";
		private final static String APP_JSON = "application/json";

		@Override
		public void initServerConnection() throws IOException {
			if(null == connectionHandler.sunHttpServer) {
				connectionHandler.sunHttpServer = HttpServer.create(new InetSocketAddress(ServerPort.HTTP_SERVER_PORT.getPort()), 0);
				connectionHandler.sunHttpServer.setExecutor(null);
				connectionHandler.sunHttpServer.createContext("/", HandlerSunHttpMsg);
			}			
		}
		
		private HttpHandler HandlerSunHttpMsg = new HttpHandler() {
			@Override
			public void handle(HttpExchange exchangeMsg) throws IOException {
					HighLevelHttpServer.this.exchangeMsg = exchangeMsg;
					handleRequestMessage(null);
			}
		};

		@Override
		public void handleRequestMessage(Channel channel) throws IOException {
			InputStream requestBodyStream =  exchangeMsg.getRequestBody();
			StringBuilder textBuilder = new StringBuilder();
			int bytesLeft = 0;
		    try (Reader reader = new BufferedReader(new InputStreamReader(requestBodyStream, 
		    							                                  Charset.forName(StandardCharsets.UTF_8.name())))) {
		    	        while ((bytesLeft = reader.read()) != -1) {
		    	            textBuilder.append((char) bytesLeft);
		    	        }
		    	    }			
			ClientInfo clientInfo = new HttpClientInfo(null, this);
			clientInfo.getConnection().sendResponse(textBuilder.toString(), clientInfo);
		}

		@Override
		public void sendResponse(String message, ClientInfo clientInfo) throws IOException {
			addContentTypeToHeaders(exchangeMsg.getResponseHeaders());
			exchangeMsg.sendResponseHeaders(HttpStatusCode.OK.getCode(), message.length());
			OutputStream outputStream = exchangeMsg.getResponseBody();
			outputStream.write(message.getBytes()); 
			outputStream.close();
		}

		private void addContentTypeToHeaders(Headers headers) {
			headers.add(CONTENT_TYPE_HEADER, APP_JSON);
		}

		@Override
		public Channel getChannel() {
			return null;
		}
		
	}
	
	/****************************************************************
	 * Message Handler
	 *****************************************************************/
		
	private class MessageHandler {
		private JsonToRunnableConvertor jsonToRunnable = new JsonToRunnableConvertor();
		private Gson convertMsg = new Gson();
		
		private void handleMessage(String message, ClientInfo clientInfo) 
													throws IOException {
			threadPool.execute(jsonToRunnable.convertToRunnable(clientInfo, message));
		}
		
		private void createJsonAndSendMsg(ClientInfo clientInfo, String msg) 
														throws IOException {
			MessageElements messageElements = new MessageElements("key", msg.trim());
			clientInfo.getConnection().sendResponse(convertMsg.toJson(messageElements), 	
													clientInfo);
		}
	}
	
	/****************************************************************
	 * JsonToRunnableConvertor
	 *****************************************************************/

	private class JsonToRunnableConvertor implements Runnable {	
		final String EXCEPTION_MSG = "Task-run(): ";
		CommandKey commandKey = null;
		String data = null;
		ClientInfo clientInfo = null;
		
		@Override
		public void run() {
			try {
				cmdFactory.create(commandKey, data).run(data, clientInfo);
			} catch (IOException e) {
				System.out.println(EXCEPTION_MSG + e.getMessage());
				connectionHandler.stopConnections();
			}
		}
		
		private Runnable convertToRunnable(ClientInfo clientInfo, String message) {
			MessageElements msgElements = messageHandler.convertMsg.
					fromJson(message.trim(), MessageElements.class);
			commandKey = CommandKey.valueOf(msgElements.getKey());
			data = msgElements.getData();
			this.clientInfo = clientInfo;
			return this;
		}
	}
	
	/****************************************************************
	 * MessageElements
	 *****************************************************************/

	private class MessageElements {
		private String Commandkey;
		private String Data;
		
		public MessageElements(String Commandkey, String Data) {
			this.Commandkey = Commandkey;
			this.Data = Data;
		}

		public String getKey() {
			return Commandkey;
		}

		public String getData() {
			return Data;
		}
	}
	
	/****************************************************************
	 * FactoryCommand
	 *****************************************************************/
	
	interface FactoryCommand {
		public String run(Object data, ClientInfo clientInfo) throws IOException;
	}
	
	/****************************************************************
	 * CompanyRegistration
	 *****************************************************************/
//	
//	public class CompanyReg implements FactoryCommandModifier{
//		private final static String COMMAND_TYPE = "commandType";
//		private final static String SUCCESS_MESSAGE = "company registered";
//		private final static String ERROR_MESSAGE = "error";
//		private final static String SYNTAX_ERROR = "sql syntax error";
//		private final static String JSON_ERROR = "invalid json";
//		private final static String CLASS_ERROR = "invalid Class";
//		private final Integer version = 1;
//		private DatabaseManagement dbManagment;
//
//		@Override
//		public void addToFactory() {
//			CMDFactory<FactoryCommand, String, Object> cmdFactory = CMDFactory.getFactoryInstance();
//			cmdFactory.add("COMPANY_REGISTRATION", (CompanyRegistration) -> new CompanyRegistration());
//		}
//
//		@Override
//		public Integer getVersion() {
//			return version;
//		}
//		
//		public class CompanyRegistration implements FactoryCommand {
//			@Override
//			public String run(Object data, ClientInfo clientInfo) {
//				String response = null;
//				JSONObject dataAsJson = (JSONObject) data;
//				try {
//					dbManagment = new DatabaseManagement(URL, USER_NAME, PASSWORD, "tadiran");
//					dbManagment.createTable(dataAsJson.getString("sqlCommand"));
//					response = createJsonResponse(COMMAND_TYPE, SUCCESS_MESSAGE);
//				} catch (JSONException e) {
//					e.printStackTrace();
//					response = createJsonResponse(ERROR_MESSAGE, JSON_ERROR);
//
//				} catch (SQLException e) {
//					e.printStackTrace();
//					response = createJsonResponse(ERROR_MESSAGE, SYNTAX_ERROR);
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//					response = createJsonResponse(ERROR_MESSAGE, CLASS_ERROR);
//				}
//				
//				return response;
//			}
//			
//			private String createJsonResponse(String message1 ,String message2) {
//				return "{" + "\"" + message1 + "\"" +":" + "\"" + message2 + "\"" + "}";
//			}
//		}
//	}
//	
//	/****************************************************************
//	 * IotUpdate
//	 *****************************************************************/
//	
//	private class IotUpdate implements FactoryCommand {
//
//		@Override
//		public String run(Object data, ClientInfo clientInfo) {
//			return null;
//		}
//	}
//	
//	/****************************************************************
//	 * IotUserRegistration
//	 *****************************************************************/
//	
//	private class IotUserRegistration implements FactoryCommand {
//
//		@Override
//		public String run(Object data, ClientInfo clientInfo) {
//			return null;
//			}
//	}
//	
//	/****************************************************************
//	 * productRegistration
//	 *****************************************************************/
//	
//	private class ProductRegistration implements FactoryCommand {
//
//		@Override
//		public String run(Object data, ClientInfo clientInfo) {
//			return null;
//			}
//	}
	
	/****************************************************************
	 * ClientInfo
	 *****************************************************************/
	
	private interface ClientInfo {
		public Channel getChannel();
		public SocketAddress getclientAddress();
		public ServerConnection getConnection();
	}
	
	/****************************************************************
	 * TcpClientInfo
	 *****************************************************************/
	
	private class TcpClientInfo implements ClientInfo {
		private SocketChannel chennelTCp;
		private ServerConnection connection;
		
		public TcpClientInfo(SocketChannel chennelTCp, 
								ServerConnection connection) {
			this.chennelTCp = chennelTCp;
			this.connection = connection;
		}
		
		@Override
		public Channel getChannel() {
			return chennelTCp;
		}

		@Override
		public SocketAddress getclientAddress() {
			return null;
		}

		@Override
		public ServerConnection getConnection() {
			return connection;
		}
	}
	
	/****************************************************************
	 * UdpClientInfo
	 *****************************************************************/
	
	private class UdpClientInfo implements ClientInfo {
		private DatagramChannel udpServer;
		private SocketAddress clientAddress;
		private ServerConnection connection;
		
		public UdpClientInfo(DatagramChannel udpServer, 
					SocketAddress clientAddress, ServerConnection connection) {
			this.udpServer = udpServer;
			this.clientAddress = clientAddress;
			this.connection = connection;
		}
		
		@Override
		public Channel getChannel() {
			return udpServer;
		}


		@Override
		public SocketAddress getclientAddress() {
			return clientAddress;
		}

		@Override
		public ServerConnection getConnection() {
			return connection;
		}
	}
	
	/****************************************************************
	 * HttpClientInfo
	 *****************************************************************/
	
	private class HttpClientInfo implements ClientInfo {
		private SocketChannel chennelTCp;
		private ServerConnection connection;
		
		public HttpClientInfo(SocketChannel chennelTCp, ServerConnection connection) {
			this.chennelTCp = chennelTCp;
			this.connection = connection;
		}
		
		@Override
		public Channel getChannel() {
			return chennelTCp;
		}

		@Override
		public SocketAddress getclientAddress() {
			return null;
		}

		@Override
		public ServerConnection getConnection() {
			return connection;
		}
	}
	
	/******************************FactoryCommandLoader****************************************************/
	/**********************************************
	 * Factory Command Loader 
	 **********************************************/
	private static class FactoryCommandLoader {
		private final static String METHOD_NAME = "addToFactory";
		private final String interfaceName = "FactoryCommandModifier";
		private JarMonitor jarDirMonitor = null;
		
		private void load(String jarFilePath) throws ClassNotFoundException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
			try {
				jarDirMonitor = new JarMonitor(jarPath);
				for(Class<?> classIter : JarLoader.load(interfaceName, jarPath)) {
					Method method = classIter.getDeclaredMethod(METHOD_NAME);
					Object instance = classIter.getDeclaredConstructor().newInstance();
					method.invoke(instance);
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | IOException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		
		public void stop() throws IOException {
			jarDirMonitor.stopUpdate();
		}
		
		public void start() {
			try {
			Callback<String> loadCallback = new Callback<>((filePath) -> {try {
				this.load(filePath);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}} ,null);
			jarDirMonitor.register(loadCallback);
				loadCommandsIntoFactory();
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		private void loadCommandsIntoFactory() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {	
		    for (final File fileEntry : new File(jarPath).listFiles()) {
		    	if(fileEntry.getName().endsWith(".jar")) {
		    		this.load(fileEntry.getPath());
		    	}

		    }
		}
	}

	/**********************************************
	 * Jar Loader 
	 **********************************************/
	private static class JarLoader {
		private static JarFile jarFile;
		private final static String DOT = ".";
		private final static String SLASH = "/";
		private final static String CLASS_EXTENSTION = ".class";
		private final static String FILE_PREFIX = "file://";
		private static List<Class<?>> classListToReturn;
		
		public static List<Class<?>> load(String interfaceName, String jarPath) throws ClassNotFoundException, IOException {
			jarFile = new JarFile(new File(jarPath));
			classListToReturn = new LinkedList<>();
			Enumeration<JarEntry> Entrylist = jarFile.entries();
			
			try(URLClassLoader classLoader = new URLClassLoader(new URL[] {new URL(FILE_PREFIX + jarPath)});){
				 while(Entrylist.hasMoreElements()) {
			        JarEntry entry = Entrylist.nextElement();

					if(entry.getName().endsWith(CLASS_EXTENSTION)) {
						Class<?> currentClass = classLoader.loadClass(getClass(entry));
						for (Class<?> classIter : currentClass.getInterfaces()) {
							if (classIter.getName().contains(interfaceName)) {
								classListToReturn.add(currentClass);
							}
						}					
					}
				}
			}
			return classListToReturn;
		}
		
		private static String getClass(JarEntry entry) {
			return entry.getName().substring(0, entry.getName().indexOf(DOT)).replace(SLASH, DOT);
		}
	}
	
	private static class JarMonitor implements DirMonitor {
		private final String JAR_EXTENSION = ".jar";
		private final String MONITOR_CHANGE_MESSAGE = "JarMonitor noticed change in: \n";
		private boolean watcherRunning = true;
		private Dispatcher<String> dispatcher;
		protected WatchService watcher;
		private File folderToWatch;
		private String dirPath;

		public JarMonitor(String dirPath) {
			try {
				this.dirPath = dirPath;
				folderToWatch = new File(dirPath);
				dispatcher = new Dispatcher<>();
					watcher = FileSystems.getDefault().newWatchService();
			new WatcherThread(folderToWatch).start();
			} catch (IOException e) {
				e.printStackTrace();
			}		}

		@Override
		public void register(Callback<String> callback) {
			dispatcher.register(Objects.requireNonNull(callback));		
		}
		
		@Override
		public void unregister(Callback<String> callback) {
			dispatcher.unregister(Objects.requireNonNull(callback));		
		}

		@Override
		public void stopUpdate() throws IOException {
			watcherRunning = false;
			watcher.close();
		}
		
		private void updateAll(WatchEvent<?> event) {
			System.out.println(MONITOR_CHANGE_MESSAGE + dirPath + event.context().toString());
			dispatcher.updateAll(dirPath + event.context().toString());
		}
		
		private	class WatcherThread extends Thread {
			public WatcherThread(File fileName) throws IOException {
				Objects.requireNonNull(fileName).toPath().register(watcher, ENTRY_MODIFY);
			}
			
			@Override
			public void run() {
				while (watcherRunning) {
					try {
						checkEvents(watcher.take());					
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ClosedWatchServiceException e){ }
				}
			}
		}
		
		private void checkEvents(WatchKey eventsKey) {
			for (WatchEvent<?> event : Objects.requireNonNull(eventsKey).pollEvents()) {
				final Path changedFile = (Path) event.context();
				if (changedFile.toString().endsWith(JAR_EXTENSION)) {
					updateAll(event);
				}
			}
			
			watcherRunning = eventsKey.reset();
		}	
	}
}