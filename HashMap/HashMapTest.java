package il.co.ilrd.HashMap;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;


class HashMapTest {

	String[] strKey = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	Integer[] strVal ={1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	
	@SuppressWarnings("rawtypes")
	@Test
	void testHashMap() {
		HashMap myMap = new HashMap();
		assertNotNull(myMap);
	}

	@SuppressWarnings("rawtypes")
	@Test
	void testHashMapInt() {
		HashMap myMap = new HashMap(10);
		assertNotNull(myMap);	
	}

	@Test
	void testClear() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		myMap.clear();
		assertEquals(0, myMap.size());
		assertTrue(myMap.isEmpty());		
	}

	@Test
	void testContainsKey() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 13; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		
		for (int i = 0; i < 13; ++i) {		
			assertTrue(myMap.containsKey(strKey[i]));
			assertFalse(myMap.containsKey(strKey[i+13]));
		}
	}

	@Test
	void testContainsValue() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 13; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		
		for (int i = 0; i < 13; ++i) {		
			assertTrue(myMap.containsValue(strVal[i]));
			assertFalse(myMap.containsValue(strVal[i+13]));
		}
	}

	@Test
	void testGet() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		assertEquals(1, myMap.get("A"));
		assertEquals(26, myMap.get("Z"));
		assertEquals(null, myMap.get("c"));
		assertEquals(6, myMap.get("F"));
	}

	@Test
	void testIsEmpty() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		assertTrue(myMap.isEmpty());
		myMap.put(strKey[0], strVal[0]);
		assertFalse(myMap.isEmpty());
	}

	@Test
	void testKeySet() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}

		for(String entry: myMap.keySet()) {
			//System.out.println(entry);
		}
	}

	@Test
	void testPut() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 13; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		assertEquals(13, myMap.size());

		for (int i = 0; i < 20; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		assertEquals(20, myMap.size());
	}

	@Test
	void testPutAll() {
		HashMap<String, Integer> myMap1 = new HashMap<String, Integer>(26);
		HashMap<String, Integer> myMap2 = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 13; ++i) {		
			myMap1.put(strKey[i], strVal[i]);
		}
		
		for (int i = 0; i < 12; ++i) {		
			myMap2.put(strKey[i+13], strVal[i+13]);
		}
		
		assertEquals(1, myMap1.get("A"));
		assertEquals(25, myMap2.get("Y"));
		assertEquals(24, myMap2.get("X"));
		assertEquals(6, myMap1.get("F"));
		
		myMap1.putAll(myMap2);
		
		assertEquals(1, myMap1.get("A"));
		assertEquals(25, myMap1.get("Y"));
		assertEquals(24, myMap1.get("X"));
		assertEquals(6, myMap1.get("F"));		
		
	}

	@Test
	void testRemove() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
				
		for (int i = 0; i < 26; i += 2) {	
			assertTrue(myMap.containsValue(strVal[i]));
			myMap.remove(strKey[i]);
		}
		
		for (int i = 0; i < 26; i += 2) {		
			assertTrue(myMap.containsValue(strVal[i + 1]));
			assertFalse(myMap.containsValue(strVal[i]));
		}
	}

	@Test
	void testSize() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		assertEquals(26, myMap.size());				
	}

	@Test
	void testValues() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}

		for(Integer entry: myMap.values()) {
			System.out.println(entry);
		}
	}

	@Test
	void testEntrySet() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		
		for (int i = 0; i < 26; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}

		for(Map.Entry<String, Integer> entry: myMap.entrySet()) {
			//System.out.println(entry);
		}
	}
	
	@Test
	void testIterator() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		Set<Entry<String, Integer>> iter = myMap.entrySet();
	}

	@Test
	void testHasNextAndNext() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>(26);
		Set<Entry<String, Integer>> iter = myMap.entrySet();
		
		assertFalse(iter.iterator().hasNext());
		
		for (int i = 0; i < 2; ++i) {		
			myMap.put(strKey[i], strVal[i]);
		}
		assertTrue(iter.iterator().hasNext());		
		iter.iterator().next();
		assertTrue(iter.iterator().hasNext());		
		iter.iterator().next();
	}
}











