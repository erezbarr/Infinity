package il.co.ilrd.dbdriver;

import java.util.List;
import java.util.Map;


public interface DbDriver {
	List<Map<String, Object>> get(ResourceType resourceType, KeyType keyType, Object value);
	List<Map<String, Object>> getAll(ResourceType resourceType);
	int add(ResourceType resourceType, Map<String, Object> resourceData);
	int edit(ResourceType resourceType, Map<String, Object> resourceData);
	void remove(ResourceType resourceType, Object key, Object value);	
} 