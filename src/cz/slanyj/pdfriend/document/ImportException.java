package cz.slanyj.pdfriend.document;

public class ImportException extends Exception {
	
	private static final long serialVersionUID = -2318967003570281216L;

	public ImportException() {
		super();
	};
	
	public ImportException(String message) {
		super(message);
	}
	
	public ImportException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ImportException(Throwable cause) {
		super(cause);
	}

}
