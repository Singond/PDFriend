package cz.slanyj.pdfriend.impose;

/**
 * A container for a SourcePage and its parent PDDocument.
 * @author Singon
 *
 */
public interface SourcePage {
	
	public <T, P> T invite(SourcePageVisitor<T, P> visitor, P param);
}
