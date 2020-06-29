package il.co.ilrd.dbdriver;

import java.util.List;
import java.util.Map;


public interface SqlDBResource {
	List<Map<String, Object>> get(KeyType key, Object value);
	List<Map<String, Object>> getAll();
	int add(Map<String, Object> resourceData);
	int edit(Map<String, Object> resourceData);
	void remove(Object key, Object value);	
}