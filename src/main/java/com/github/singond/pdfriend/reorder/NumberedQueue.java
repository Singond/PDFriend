package com.github.singond.pdfriend.reorder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A collection of queues identified by numbers.
 *
 * @param <T> the type of objects stored in the queues
 */
class NumberedQueue<T> {

	private final SortedMap<Integer, Queue<T>> data;

	public NumberedQueue() {
		data = new TreeMap<>();
	}

	/**
	 * Puts an object onto the end of a specified queue.
	 *
	 * @param element the object to be added
	 * @param queueNumber the number identifying the queue to which
	 *        {@code element} should be added
	 * @return value specified by {@link Collection#add}
	 */
	public boolean add(T element, int queueNumber) {
		if (element == null) {
			throw new NullPointerException("The object being added is null");
		}
		return getBucket(queueNumber).add(element);
	}

	public int queueSize(int queueNumber) {
		if (data.containsKey(queueNumber)) {
			return getBucket(queueNumber).size();
		} else {
			return 0;
		}
	}

	public int size() {
		int total = 0;
		for (Queue<T> q : data.values()) {
			if (q != null) {
				total += q.size();
			}
		}
		return total;
	}

	/**
	 * Returns a list where each element stored is represented by the number
	 * with which it was added.
	 *
	 * @return a list of all elements represented by their associated number
	 */
	public List<Integer> numbers() {
		List<Integer> result = new ArrayList<>(size());
		for (Entry<Integer, Queue<T>> e : data.entrySet()) {
			Integer number = e.getKey();
			for (int i = 0; i < e.getValue().size(); i++) {
				result.add(number);
			}
		}
		return result;
	}

	/**
	 * Returns the next object in the given queue.
	 *
	 * @param queueNumber the number identifying the queue
	 * @return the next element in queue {@code queueNumber}
	 * @throws NoSuchElementException if queue {@code queueNumber} is empty
	 */
	public T nextInQueue(int queueNumber) {
		if (!data.containsKey(queueNumber)) {
			throw new NoSuchElementException("No list of length " + queueNumber);
		}
		return getBucket(queueNumber).remove();
	}

	/**
	 * Returns all non-null elements, iterating the queues in ascending order.
	 */
	public List<T> getAllAscending() {
		List<T> result = new ArrayList<>(size());
		for (Queue<T> q : data.values()) {
			while (!q.isEmpty()) {
				T t = q.remove();
				if (t != null) result.add(t);
			}
		}
		return result;
	}

	private Queue<T> getBucket(int number) {
		Integer n = Integer.valueOf(number);
		if (data.containsKey(n)) {
			return data.get(n);
		} else {
			Queue<T> q = new ArrayDeque<>();
			data.put(n, q);
			return q;
		}
	}
}
