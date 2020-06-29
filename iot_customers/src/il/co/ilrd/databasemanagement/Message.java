package il.co.ilrd.databasemanagement;

public interface Message<K, V> {
	
	public K getKey();
	public V getData();
	
}