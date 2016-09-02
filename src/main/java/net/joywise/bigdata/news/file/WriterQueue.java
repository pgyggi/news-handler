package net.joywise.bigdata.news.file;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WriterQueue {

	private static final int MAX_QUEUE_SIZE = 5000;
	private LinkedList<String> queue = new LinkedList<String>();
	private Lock lock = new ReentrantLock();
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	private static WriterQueue manager = new WriterQueue();

	private WriterQueue() {

	}

	public static WriterQueue getQueue() {
		return manager;
	}

	public void put(String phone) {
		lock.lock();
		try {
			while (queue.size() == MAX_QUEUE_SIZE) {
				System.out.println("warning: data queue is full!");
				notFull.await();
			}
			queue.addFirst(phone);
			notEmpty.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public LinkedList<String> takeAll() {
		LinkedList<String> retVal = new LinkedList<String>();
		lock.lock();
		try {
			while (queue.size() == 0) {
				System.out.println("warning: data queue is empty!");
				notEmpty.await();
			}
			retVal.addAll(queue);
			queue.clear();
			notFull.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return retVal;
	}
}