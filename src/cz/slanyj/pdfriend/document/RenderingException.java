package cz.slanyj.pdfriend.document;

/**
 * An exception signifying an error during rendering process.
 * @author Singon
 *
 */
public class RenderingException extends Exception {

	private static final long serialVersionUID = -2909204466624598226L;
	
	public RenderingException() {
		super();
	};
	
	public RenderingException(String message) {
		super(message);
	}
	
	public RenderingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public RenderingException(Throwable cause) {
		super(cause);
	}

}
