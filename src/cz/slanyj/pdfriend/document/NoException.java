package cz.slanyj.pdfriend.document;

/**
 * Class signifying that no exceptions can be thrown from a piece of code.
 * This is to satisfy Visitors which require an exception class thrown
 * by their implementations as an argument, when the given implementation
 * does not produce an exceptions.
 * @author Singon
 *
 */
@SuppressWarnings("serial")
public class NoException extends RuntimeException {

	/** Not instantiable: Will never be thrown. */
	private NoException(){};
}
