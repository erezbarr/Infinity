package il.co.ilrd.DatabaseManagement;

import java.io.Serializable;
import il.co.ilrd.chatserver.Message;

public class DatabaseManagementMessage implements Message<ActionTypeKey, Object[]> , Serializable{
	private static final long serialVersionUID = 1L;
	private ActionTypeKey key;
	private Object[] data;
	
	public DatabaseManagementMessage (ActionTypeKey key, Object[] data) {
		this.key = key;
		this.data = data;
	}
	
	@Override
	public ActionTypeKey getKey() {
		return key;
	}
	
	@Override
	public Object[] getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return " " + data;
	}	
}