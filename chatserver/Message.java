package il.co.ilrd.chatserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface  Message<K, V> {
	
	public K getKey();
	public V getData();
	
	public static byte[] toByteArray(Object obj) throws IOException {
	      byte[] returnBytesArray = null;
	      ByteArrayOutputStream byteArrayOutPut = null;
	      ObjectOutputStream ObjectOutPut = null;
	      try {
	    	  byteArrayOutPut = new ByteArrayOutputStream();
	    	  ObjectOutPut = new ObjectOutputStream(byteArrayOutPut);
	    	  ObjectOutPut.writeObject(obj);
	    	  ObjectOutPut.flush();
	    	  returnBytesArray = byteArrayOutPut.toByteArray();
	      } finally {
	          if (ObjectOutPut != null ) {
	        	  ObjectOutPut.close();
	          }
	          if (byteArrayOutPut != null) {
	        	  byteArrayOutPut.close();
	          }
	      }
	      return returnBytesArray;
	  }
	
	 public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
	      Object returnObject = null;
	      ByteArrayInputStream byteArrayInPut = null;
	      ObjectInputStream ObjectInPut = null;
	      try {
	    	  byteArrayInPut = new ByteArrayInputStream(bytes);
	    	  ObjectInPut = new ObjectInputStream(byteArrayInPut);
	          returnObject = ObjectInPut.readObject();
	      } finally {
	          if (byteArrayInPut != null) {
	        	  byteArrayInPut.close();
	          }
	          if (ObjectInPut != null) {
	        	  ObjectInPut.close();
	          }
	      }
	      return returnObject;
	  }
}