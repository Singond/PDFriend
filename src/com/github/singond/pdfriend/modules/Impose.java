package com.github.singond.pdfriend.modules;

import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.imposition.Booklet;
import com.github.singond.pdfriend.imposition.NUp;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Lay out pages of the source documents onto pages of a new document")
public class Impose implements Module {

	/** A pre-defined type of imposition: booklet, n-up etc. */
	private Type type;
	/** Specifies where the binding is located */
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	/** In a vertical booklet, print the verso upside down. */
	private boolean flipVerso = false;
	/** Number of output pages */
	private int pages = -1;
	
	private static ExtendedLogger logger = Log.logger(Impose.class);
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Booklet.Binding getBinding() {
		return binding;
	}

	public void setBinding(Booklet.Binding binding) {
		this.binding = binding;
	}

	public boolean isFlipVerso() {
		return flipVerso;
	}

	public void setFlipVerso(boolean flipVerso) {
		this.flipVerso = flipVerso;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	@Override
	public ModuleData process(ModuleData data) throws RenderingException {
		logger.info("*** PDFriend Impose ***");
		if (type == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Selected imposition type is: " + type.getName());
		VirtualDocument document = type.impose(data.asSingleDocument());
		return ModuleDataFactory.of(document);
	}
	
	private VirtualDocument imposeBooklet(VirtualDocument source)
			throws RenderingException {
		logger.info("Imposing booklet...");
		
		Booklet booklet = Booklet.from(source, binding, flipVerso);
		Volume volume = booklet.volume();
		SourceProvider sp = new SequentialSourceProvider(source);
		sp.setSourceTo(volume.pages());
		VirtualDocument imposed = volume.renderDocument();
		return imposed;
	}
	
	private VirtualDocument imposeNUp(VirtualDocument source, int rows, int columns)
			throws RenderingException {
		logger.info("Imposing n-up...");
		int pages = this.pages;
		
		NUp.Builder nup = new NUp.Builder();
		nup.setRows(rows);
		nup.setCols(columns);
		if (pages > 0) {
			nup.setNumberOfPages(pages);
		}
		VirtualDocument imposed = nup.buildFor(source).getDocument();
		return imposed;
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 * Each class of this interface represents one type of imposed document
	 * and provides its complete implementation.
	 */
	public interface Type {
		/** Returns a user-friendly name of the imposition type. */
		public String getName();
		
		/**
		 * Performs the imposition task defined by this Type class,
		 * using the settings in the outer Impose object and the
		 * @param doc the document whose pages are to be imposed onto
		 *        this document
		 * @return the imposed document as a new instance of virtual document
		 */
		public VirtualDocument impose(VirtualDocument doc)
				throws RenderingException;
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 */
	public class TypeBooklet implements Type {
		/** The name of the document type */
		private static final String name = "booklet";
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public VirtualDocument impose(VirtualDocument doc) throws RenderingException {
			return imposeBooklet(doc);
		}
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 */
	public class TypeNUp implements Type {
		/** The name of the document type */
		private static final String name = "n-up";
		/** Then number of rows in the n-up grid */
		private final int rows;
		/** Then number of columns in the n-up grid */
		private final int columns;
		
		public TypeNUp(int rows, int columns) {
			this.rows = rows;
			this.columns = columns;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public VirtualDocument impose(VirtualDocument doc) throws RenderingException {
			return imposeNUp(doc, rows, columns);
		}
	}
}
