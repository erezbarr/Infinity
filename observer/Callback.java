package il.co.ilrd.observer;

import java.util.function.Consumer;

public class Callback <T>{
	private Dispatcher <T> dispatcher;	
	private Consumer<T> updateCallback;
	private Consumer<T> stopUpdateCallback;

	public Callback(Consumer<T> updateCallback, Consumer<T> stopUpdateCallback) {
		this.updateCallback = updateCallback;
		this.stopUpdateCallback = stopUpdateCallback;
	}

	public void update(T param) {
		if (null != updateCallback) {
			updateCallback.accept(param);
		}	
	}
	
	public void setUpdateCallback(Consumer<T> updateCallbackFuncConsumer) {
		updateCallback = updateCallbackFuncConsumer;
	}
	
	public void setstopUpdateCallback(Consumer<T> updatestopUpdateCallbackFuncConsumer) {
		stopUpdateCallback = updatestopUpdateCallbackFuncConsumer;
	}	
	
	public void stopUpdate() {
		if (stopUpdateCallback != null) {
			stopUpdateCallback.accept(null);
		}
	}
	
	public Dispatcher <T> getDispatcher() {
		return dispatcher;
	}
	
	public void setDispatcher(Dispatcher <T> dispatcher) {
		if (null != dispatcher ) {
			this.dispatcher = dispatcher;
		}
	}
}