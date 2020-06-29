package il.co.ilrd.observer;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher<T> {
	private List<Callback<T>> callbackList = new ArrayList<>();
	
	public void register(Callback<T> callback) {
		callback.setDispatcher(this);
		callbackList.add(callback);
	}
	public void unregister(Callback<T>  callback) {
		callback.setDispatcher(null);
		callbackList.remove(callback);
	}
	public void updateAll(T param) {
		for(Callback<T> caller : callbackList) {
			caller.update(param);
		}
	}
	
	public void stopUpdate() {
		for(Callback<T> caller : callbackList) {
			caller.stopUpdate();
		}
	}
	
}