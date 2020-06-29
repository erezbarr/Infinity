package il.co.ilrd.chatserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public interface GlobalMessage<K, D> {
	
	public K getKey();
	public D getData();
	static GlobalMessage<Integer, GlobalMessage<?, ?>> message = null;
	
	public static GlobalMessage<Integer, GlobalMessage<?, ?>> toObject(byte[] byteArr) {
		Object obj = null;
        try(ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
        	ObjectInputStream ois = new ObjectInputStream(bis);) {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

        return (GlobalMessage<Integer, GlobalMessage<?, ?>>) obj;
	}	
}