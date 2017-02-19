package cz.slanyj.pdfriend.book;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of element ordering.
 * Ensures that every element is represented only once in the order,
 * their indices are unique within this object and the indices
 * form a "continuous" sequence from zero up with increment = 1.
 * @param T Type of the elements to be ordered.
 * 
 * @author Singon
 */
public class Order<T> {
	/** The next available index */
	private int index = 0;
	/** A map keeping key-value pairs of element-index. */
	private Map<T, Integer> orderMap = new HashMap<>();
	
	/**
	 * Adds the given element as the next in order.
	 * @throw UnsupportedOperationException If the element is already
	 * present in this Order object.
	 */
	public void addNext(T t) {
		// Place the Leaf in the order only if it is not present yet
		boolean wasPresent = orderMap.putIfAbsent(t, index++)==null;
		if (wasPresent) {
			throw new UnsupportedOperationException
				("The element has already been added to this Order object: "+t);
		}
	}
	
	/** Returns the index of the given element. */
	public int indexOf(T t) {
		return orderMap.get(t);
	}
	
	/** Returns true if the element is present in this order. */ 
	public boolean hasElement(T t) {
		return orderMap.containsKey(t);
	}
}
