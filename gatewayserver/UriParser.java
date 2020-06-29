package il.co.ilrd.gatewayserver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

class UriParser {
	private final static int HEADER = 0;
	private final static int VALUE = 2;
	private final static int DB_NAME = 2;
	private final static int TABLE_NAME = 3;
	private final static int SPLIT_LIMIT = 4;
	private static String tokensSplitString[] = null;
	
	
	public static String getDbName(URI uri) {
		tokensSplitString = uri.getRawPath().split("/", SPLIT_LIMIT);
	
		return tokensSplitString[DB_NAME];
	}
	
	public static String getTableName(URI uri) {
		tokensSplitString = uri.getRawPath().split("/", SPLIT_LIMIT);

		return tokensSplitString[TABLE_NAME];
	}

	public static Map<String,String> getQueryMap(URI uri) {
		Map<String,String> queryMap = new HashMap<>();
		String[] queryPair = null;
		
		tokensSplitString = uri.getRawQuery().split("&");
		for (String queryPairsString : tokensSplitString) {
			queryPair = queryPairsString.split("=");
			queryMap.put(queryPair[HEADER], queryPair[VALUE]);
		}
		
		return queryMap;
	}
}