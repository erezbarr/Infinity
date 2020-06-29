package il.co.ilrd.observer;

import java.util.function.Consumer;

public class Observer {
	
	private Callback<Integer> callback;
	
	public void regitser(Subject subject) {
		Consumer<Integer> update = (param) -> {System.out.println("Updating: " + param);};
		Consumer<Integer> stopupdate = (param) -> {System.out.println("Stop Update: " + param);}; 
		callback = new Callback<Integer>(update, stopupdate);
		subject.register(callback);
	}
	
	public void unregitser() {
		callback.getDispatcher().unregister(callback);
	}	
	
	public void unregitser(Subject subject) {
		subject.unregister(callback);
	}
}