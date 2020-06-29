package il.co.ilrd.servlets;

import java.util.UUID;

public class Session {
	private final String token;
	private long tsLastActivity;
	
	public Session() {
		token = UUID.randomUUID().toString();
		tsLastActivity = System.currentTimeMillis();
	}

	public long getTsLastActivity() {
		return tsLastActivity;
	}

	public void updateTsLastActivity() {
		tsLastActivity = System.currentTimeMillis();
	}

	public String getToken() {
		return token;
	}
}