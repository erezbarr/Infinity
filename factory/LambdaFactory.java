package il.co.ilrd.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LambdaFactory <T, K, D> {
	private Map<K, Function<D, ? extends T>> map = new HashMap<K, Function<D, ? extends T>>(10);
	
	public void add(K key, Function<D, ? extends T> func) {
		map.put(key, func);
	}

	public T create(K key, D data) {
		return map.get(key).apply(data);
	}
	
	public T create(K key) {
		return create(key, null); 
	}
}

