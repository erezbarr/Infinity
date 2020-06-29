package il.co.ilrd.pingpong.handlers;
import java.io.Serializable;

public class PingPongMessage implements Message<Integer, Message<String, Void>>, Serializable  {
	private static final long serialVersionUID = 1L;
	private Integer key;
	private Message<String, Void> data;
	
	PingPongMessage(int protocolKey, String data){
		this.data = new InnerMessage(data, null);
		this.key = protocolKey;
	}
	
	public PingPongMessage(String string, Object data2) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer getKey() {
		return key;
	}
	@Override
	public Message<String, Void> getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return "Key is: " + key.toString() + " Data is: " +  data.toString();
	}
	
/******************************************************************************/
	
	private class InnerMessage implements Message<String, Void>, Serializable{
	private static final long serialVersionUID = 1L;
	private	String key;
	private	Void data;
		
		InnerMessage(String key, Void data){
			this.key = key;
			this.data = data;
		}
		
		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Void getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return key.toString();
		}
	}
}
