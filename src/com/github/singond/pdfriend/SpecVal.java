package com.github.singond.pdfriend;

/**
 * A wrapper class which enables a value type and a pre-defined set of
 * "special" values to be merged into one type.
 * <p>
 * This can be used in cases where some property can take an unusual value,
 * which cannot be expressed easily by the value type.
 * Consider length as an example:
 * The property can, in most cases, be expressed by some number (e.g. double),
 * representing the length, but it may be desirable to also define
 * an additional value, like "automatic", to indicate that length is to be
 * determined from other properties.
 * One can use {@code null} to indicate this value in the program, but if
 * more than one such special value arise, one must come up with
 * a different representation, like listing all special values in an enum
 * and maintaining two fields: one for the special values and one for the
 * regular values. The purpose of this class is to hide this duality
 * and present the special and regular values as one type.
 * <p>
 * The value type can be any class, while the set of special values is
 * represented by an enum. The special values must also define one constant
 * which represents the regular values.
 *
 * @author Singon
 * @param <T> an enum listing all the special values
 * @param <V> the value type
 */
public abstract class SpecVal<T extends Enum<T>, V> {
	
	/** The special value */
	private final T special;
	
	/** The regular value */
	private final V value;
	
	/** The single special value which represents a regular value */
	private final T valueConstant = getValueConstant();
	
	/** Constructs an instance representing a special value. */
	protected SpecVal(T special) {
		if (special == null) {
			throw new NullPointerException("The special value must not be null");
		}
		
		this.special = special;
		this.value = null;
	}
	
	/** Constructs an instance representing a regular value. */
	protected SpecVal(V value) {
		if (value == null) {
			throw new NullPointerException("The value must not be null");
		}
		
		this.special = getValueConstant();
		this.value = value;
	}
	
	protected abstract T getValueConstant();
	
	public T special() {
		return special;
	}
	
	public boolean isValue() {
		return special == valueConstant;
	}
	
	public V value() {
		if (special == valueConstant) {
			return value;
		} else {
			throw new UnsupportedOperationException("Cannot obtain regular value from special values");
		}
	}
}
