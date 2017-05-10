package cz.slanyj.pdfriend.book.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cz.slanyj.pdfriend.book.model.Page;
import cz.slanyj.pdfriend.book.model.SinglePage;
import cz.slanyj.pdfriend.document.NoException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.document.VirtualPage;

/**
 * Assigns VirtualPages to Page objects by visiting Pages in their order,
 * putting VirtualPages into them sequentially.
 *
 * @author Singon
 *
 */
public class SequentialSourceProvider implements SourceProvider, PageVisitor<Void, Void, NoException> {

	private final Queue<VirtualPage> sourcePages;
	
	public SequentialSourceProvider(VirtualDocument doc) {
		sourcePages = new LinkedList<>(doc.getPages());
	}
	
	/**
	 * Uses the given queue of pages directly without creating new object.
	 * @param pages
	 */
	SequentialSourceProvider(Queue<VirtualPage> pages) {
		sourcePages = pages;
	}

	@Override
	public void setSourceTo(List<Page> pages) {
		for (Page p : pages) {
			p.invite(this, null);
		}
	}
	
	@Override
	public Void visit(SinglePage p, Void param) throws NoException {
		p.setSource(sourcePages.poll());
		return null;
	}
}
