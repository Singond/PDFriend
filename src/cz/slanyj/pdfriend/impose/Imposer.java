package cz.slanyj.pdfriend.impose;

/**
 * An interface for objects which build the final document by placing
 * pages of the source document into pages of the output document.
 * Each class using this interface provide implementation for a specific
 * type of the output document.
 * @author Singon
 *
 */
public interface Imposer extends SourcePageVisitor<Void, Void> {

	/**
	 * Renders a the given target sheet into the document.
	 * 
	 * @param sheet The TargetSheet to be rendered.
	 */
	public void renderSheet(TargetSheet sheet);
	
}
