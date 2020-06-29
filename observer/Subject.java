package il.co.ilrd.observer;

public class Subject {
	
	private Dispatcher  <Integer> dispatcher = new Dispatcher<>();
	
	public void register(Callback <Integer> callback) {
		dispatcher.register(callback);
	}
	public void unregister(Callback <Integer> callback) {
		dispatcher.unregister(callback);
	}
	public void updateAll(Integer param) {
		dispatcher.updateAll(param);
	}
	public void stopUpdate() {
		dispatcher.stopUpdate();
	}
}