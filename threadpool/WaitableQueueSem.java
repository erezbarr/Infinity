package il.co.ilrd.threadpool;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitableQueueSem<T> {
	
	private PriorityQueue<T> queue = new PriorityQueue<>();
	private Semaphore queueSizeSem = new Semaphore(0);
	private Lock queueLock = new  ReentrantLock();

	public WaitableQueueSem() {
		this(null);
	}
	
	public WaitableQueueSem(Comparator<T> comp) {
		queue = new PriorityQueue<>(comp);
	}
	
	public void enqueue (T elem) {		
		queueLock.lock();
		queue.add(elem);
		queueLock.unlock();
		queueSizeSem.release();
	}

	public T dequeue() throws InterruptedException {

		queueSizeSem.acquire();
		queueLock.lock();
		T elem = queue.poll();
		queueLock.unlock();
		
		return elem;
	}
	
	public T dequeue(long timeout,  TimeUnit timeunit) throws InterruptedException {
		T elem = null;
		
		if (queueSizeSem.tryAcquire(timeout, timeunit)) {
			queueLock.lock();
			elem = queue.poll();
			queueLock.unlock();
		}
		
		return elem;
	}
	
	public boolean remove (T elem) throws InterruptedException {
		Boolean hasRemoved;
		
		queueLock.lock();
		hasRemoved = queue.remove(elem);
		queueLock.unlock();

		if(hasRemoved) {
			queueSizeSem.acquire();
		}
		
		return hasRemoved;
	}
}
