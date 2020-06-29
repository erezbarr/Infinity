package il.co.ilrd.HashMap;

import java.util.*;

import il.co.ilrd.Pair.Pair;;

public class HashMap<K,V> implements Map<K, V> {

	private List<List<Pair<K, V>>> hashMap;
	private final int capacity;
	
	public HashMap() {
		this(16);
	}
	
	public HashMap(int capcacity) {
		this.capacity = capcacity;
		initMap();
	}
	
	private void initMap() {
		hashMap = new ArrayList<>(capacity);
		
		for (int i = 0; i < capacity; ++i) {
			hashMap.add(new LinkedList<Pair<K, V>>());
		}
	}
	
	@Override
	public void clear() {
		for(List<Pair<K, V>> list: hashMap) {
			list.clear();
		}		
	}

	@Override
	public boolean containsKey(Object key) {
		if(null == get(key)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean containsValue(Object value) {		
		for(List<Pair<K, V>> list: hashMap) {
			for(Pair<K, V> obj: list) {
				if (obj.getValue() == value) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	@Override
	public V get(Object key) {
		V holder_V = null;
		List<Pair<K, V>> bucket = getBucket(key);
		
		for (Pair<K,V> pair: bucket) {
			if (pair.getKey() == key) {
				holder_V = pair.getValue();
				break;
			}
		}
			
		return holder_V;
	}

	@Override
	public boolean isEmpty() {
		for(List<Pair<K, V>> list: hashMap) {
			if (!list.isEmpty()) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	@Override
	public V put(K key, V value) {
		V holder_V = null;
		List<Pair<K, V>> bucket = getBucket(key);
		
		for (Pair<K,V> pair: bucket) {
			if (pair.getKey().equals(key)) {
				holder_V = pair.getValue();
				pair.setValue(value);
				
				return holder_V;
			}
		}
		bucket.add(Pair.oF(key, value));
			
		return holder_V;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> hashMap) {
		
		for(Map.Entry<? extends K, ? extends V> entry: hashMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		V remove_V = null;
		List<Pair<K, V>> bucket = getBucket(key);
		
		for (Pair<K,V> pair: bucket) {
			if (pair.getKey().equals(key)) {
				remove_V = pair.getValue();
				bucket.remove(pair);
				break;
			}
		}
			
		return remove_V;
	}

	@Override
	public int size() {
		int counter = 0;
		
		for(List<Pair<K, V>> list: hashMap) {
			counter += list.size();
		}		
		
		return counter;
	}

	@Override
	public Collection<V> values() {
		return new ValSet();
	}

	private List<Pair<K, V>> getBucket (Object arg0) {
		return hashMap.get(arg0.hashCode() % capacity);
	}

	public class KeySet extends AbstractSet<K> {

		@Override
		final public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public int size() {
			return this.size();
		}

	}	
	
	public class KeyIterator implements Iterator<K> {
		Iterator<Map.Entry<K, V>> iter = new EntrySet().iterator();

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public K next() {
			return iter.next().getKey();
		}
		
	}		

	public class ValSet extends AbstractSet<V> {

		@Override
		public Iterator<V> iterator() {
			return new ValIterator();
		}

		@Override
		public int size() {
			return this.size();
		}

	}	
	
	public class ValIterator implements Iterator<V> {
		Iterator<Map.Entry<K, V>> iter = new EntrySet().iterator();

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public V next() {
			return iter.next().getValue();
		}
		
	}	
	
	private class EntrySet extends AbstractSet<Map.Entry<K, V>>{

		@Override
		final public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return this.size();
		}
		
		private class EntryIterator implements Iterator<Map.Entry<K, V>>{
			Iterator<List<Pair<K, V>>> outer = hashMap.iterator();
			Iterator<Pair<K, V>> inner = outer.next().iterator();
			
			{
				while (!inner.hasNext() && outer.hasNext()) {
					inner = outer.next().listIterator();
				}
			}		
			
			@Override
			public boolean hasNext() {				
				return outer.hasNext() || inner.hasNext();
			}

			@Override
			public Entry<K, V> next() {	
				Pair<K, V> previous = inner.next();
				
				while (!inner.hasNext() && outer.hasNext()) {
					inner = outer.next().listIterator();
				}
				return previous;
			}	
		}
	}
}
