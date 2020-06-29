package il.co.ilrd.servlets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import il.co.ilrd.dbdriver.DriverUtils;

public class SessionManagment {
	private final static long MAX_TIME = 3600000; 
	private static Map<Integer , Session> sessionMap = new ConcurrentHashMap<>();
	
	private static class SessionManagementHolder {
		public static SessionManagment instance = new SessionManagment();
	}
	
	public static SessionManagment getInstance() {
		return SessionManagementHolder.instance;
	}
	
	public Session addSession(int userId) {
		Session session = new Session();
		sessionMap.put(userId, session);
		return session;
	}
	
	public boolean isTokenValid(int userId, String token) {
		Session session = sessionMap.get(userId);
		if((session != null) && 
			session.getToken().equals(token) &&
			(System.currentTimeMillis() - session.getTsLastActivity() < MAX_TIME)) {
				return true;
			}
		return false;
	}
	
	public boolean isLoginValid(List<Map<String, Object>> result, String pass, String userType) {
		return (result.get(0).get(DriverUtils.PASSWORD).equals(pass) && result.get(0).get(DriverUtils.USER_TYPE).equals(userType));
	}
}