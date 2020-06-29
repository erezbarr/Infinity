package il.co.ilrd.raspi_clients;


public class IOTUpdateMessage implements Comparable<IOTUpdateMessage> {
	private static final String ID_VALUE_DELIMITER = "$";
	private String id;
	private String value;
	private int retransmissionCounter;
	private long sendingInterval;
	private long timeToNextSend;
	private long timeSent;
	
	public IOTUpdateMessage(String id, String value, int retransmissionCounter, int sendingInterval) {
		this.id = id;
		this.value = value;
		this.retransmissionCounter = retransmissionCounter;
		this.sendingInterval = sendingInterval;
		timeToNextSend = System.currentTimeMillis() + sendingInterval;
	}
	
	public void setTimeSent(long timeSent) {
		this.timeSent = timeSent;
	}
	
	public long getTimeSent() {
		return timeSent;
	}
	
	public long getTimeToSend() {
		return timeToNextSend;
	}
	
	public String getID() {
		return id;
	}
	
	public int decreaseCounter() {
		--retransmissionCounter;
		return retransmissionCounter;
	}
	
	public int getRetransmissionCounter() {
		return retransmissionCounter;
	}
	
	public long getInterval() {
		return sendingInterval;
	}
	
	public String getMessage() {
		return id + ID_VALUE_DELIMITER + value;		
	}
	
	public void updateTimeToSend() {
		timeToNextSend = System.currentTimeMillis() + sendingInterval;
	}

	@Override
	public int compareTo(IOTUpdateMessage msg) {
		return (int) (msg.timeToNextSend - this.timeToNextSend);
	}

	public void updateTimeToSend(long l) {
		sendingInterval = l;
		updateTimeToSend();
	}
	
}