package com.github.singond.pdfriend.modules;

import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Page;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.PageSize;
import com.github.singond.pdfriend.geometry.PageSize.FitToLargest;
import com.github.singond.pdfriend.geometry.PageSize.Scale;
import com.github.singond.pdfriend.imposition.Booklet;
import com.github.singond.pdfriend.imposition.NUp;
import com.github.singond.pdfriend.imposition.Overlay;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
public class Impose implements Module {

	/** A pre-defined type of imposition: booklet, n-up etc. */
	private Type type;
	/** Specifies where the binding is located */
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	/** In a vertical booklet, print the verso upside down. */
	private boolean flipVerso = false;
	/** Number of output pages */
	private int pages = -1;
	/** Size to be applied to pages of input */
	private PageSize pageSize;
	
	/** The unit used in working with book object model */
	public static final LengthUnit LENGTH_UNIT = LengthUnits.POINT_POSTSCRIPT;
	
	/** Logger instance */
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

	public PageSize getSize() {
		return pageSize;
	}

	public void setSize(PageSize size) {
		this.pageSize = size;
	}

	/**
	 * Returns a default PageSize.Visitor to provide resizing for pages.
	 * @param docs the list of virtual documents to be used in evaluating the
	 *        scale. This will usually be the list of documents being imposed.
	 * @return a PageSize.Visitor instance which returns the scale value
	 */
	public PageSize.Visitor<Double, VirtualPage> getDefaultPageSizer(final List<VirtualDocument> docs) {
		return new PageSize.Visitor<Double, VirtualPage>() {
			private final List<VirtualDocument> documents = docs;
			
			@Override
			public Double visit(Scale size, VirtualPage page) {
				return size.scalePage();
			}

			@Override
			public Double visit(FitToLargest size, VirtualPage page) {
				return size.scalePage(page, VirtualDocument.concatenate(documents).getPages());
			}
		};
	}

	@Override
	public ModuleData process(ModuleData data) throws ModuleException  {
		logger.info("*** PDFriend Impose ***");
		if (type == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Selected imposition type is: " + type.getName());
		VirtualDocument document;
		try {
			document = type.impose(data);
			return ModuleDataFactory.of(document);
		} catch (RenderingException e) {
			throw new ModuleException(e);
		}
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
		 * @param data a ModuleData object containing the documents whose
		 *        pages are to be imposed onto this document
		 * @return the imposed document as a new instance of virtual document
		 */
		public VirtualDocument impose(ModuleData data)
				throws RenderingException;
	}
	
	/**
	 * A booklet document.
	 */
	public class TypeBooklet implements Type {
		/** The name of the document type */
		private static final String name = "booklet";
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public VirtualDocument impose(ModuleData data) throws RenderingException {
			logger.info("Imposing booklet...");
			VirtualDocument source = data.asSingleDocument();
			Booklet booklet = Booklet.from(source, binding, flipVerso);
			Volume volume = booklet.volume();
			SourceProvider<Page> sp = new SequentialSourceProvider(source);
			sp.setSourceTo(volume.pages());
			VirtualDocument imposed = volume.renderDocument();
			return imposed;
		}
	}
	
	/**
	 * An n-up document.
	 */
	public class TypeNUp implements Type {
		/** The name of the document type */
		private static final String name = "n-up";
		/** The number of rows in the n-up grid */
		private final int rows;
		/** The number of columns in the n-up grid */
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
		public VirtualDocument impose(ModuleData data) throws RenderingException {
			VirtualDocument source = data.asSingleDocument();
			logger.info("Imposing n-up...");
			int pages = Impose.this.pages;
			
			NUp.Builder nup = new NUp.Builder();
			nup.setRows(rows);
			nup.setCols(columns);
			nup.setPageSize(pageSize);
			if (pages > 0) {
				nup.setNumberOfPages(pages);
			}
			VirtualDocument imposed = nup.buildFor(source).getDocument();
			return imposed;
		}
	}
	
	/**
	 * A layered document (several documents whose corresponding pages are overlaid).
	 */
	public class TypeOverlay implements Type {
		/** The name of the document type */
		private static final String name = "overlay";
		/** Number of layers on each page */
		private final int layers;
		
		public TypeOverlay(int layers) {
			this.layers = layers;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public VirtualDocument impose(ModuleData data) throws RenderingException {
			logger.info("Imposing overlay...");
			List<VirtualDocument> docs = data.asMultipleDocuments();
			Overlay model = Overlay.from(docs);
			VirtualDocument imposed = model.getDocument();
			return imposed;
		}
	}
}
