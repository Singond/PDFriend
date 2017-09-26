package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.Page;
import com.github.singond.pdfriend.book.SequentialSourceProvider;
import com.github.singond.pdfriend.book.SourceProvider;
import com.github.singond.pdfriend.book.Volume;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleDataFactory;
import com.github.singond.pdfriend.modules.ModuleException;

/**
 * The imposition module of PDFriend.
 * @author Singon
 *
 */
public class Imposition implements Module {

	/**
	 * The imposition task containing all settings needed.
	 * These include preprocessor settings and settings specific to this
	 * imposition type.
	 */
	private Imposable task;
	/** Specifies where the binding is located */
	@Deprecated
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	/** In a vertical booklet, print the verso upside down. */
	@Deprecated
	private boolean flipVerso = false;
	/** Number of output pages */
	@Deprecated
	private int pages = -1;
	/** Pre-processing settings like scale, rotation or resizing */
	@Deprecated
	private Preprocessor.Settings preprocess;
	
	/** The unit used in working with book object model */
	public static final LengthUnit LENGTH_UNIT = LengthUnits.POINT_POSTSCRIPT;
	
	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(Imposition.class);
	
	public Imposable getTask() {
		return task;
	}

	public void setTask(Imposable task) {
		this.task = task;
	}

	@Deprecated
	public Booklet.Binding getBinding() {
		return binding;
	}

	@Deprecated
	public void setBinding(Booklet.Binding binding) {
		this.binding = binding;
	}

	@Deprecated
	public boolean isFlipVerso() {
		return flipVerso;
	}

	@Deprecated
	public void setFlipVerso(boolean flipVerso) {
		this.flipVerso = flipVerso;
	}

	@Deprecated
	public int getPages() {
		return pages;
	}

	@Deprecated
	public void setPages(int pages) {
		this.pages = pages;
	}

	@Deprecated
	public Preprocessor.Settings getPreprocessing() {
		return preprocess;
	}

	@Deprecated
	public void setPreprocessing(Preprocessor.Settings preprocess) {
		this.preprocess = preprocess;
	}

	@Override
	public ModuleData process(ModuleData data) throws ModuleException  {
		logger.info("*** PDFriend Impose ***");
		if (task == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Selected imposition type is: " + task.getName());
		
		VirtualDocument document;
		if (task.prefersMultipleInput()) {
			document = task.impose(data.asMultipleDocuments());
		} else {
			document = task.impose(data.asSingleDocument());
		}
		return ModuleDataFactory.of(document);
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 * Each class of this interface represents one type of imposed document
	 * and provides its complete implementation.
	 */
	@Deprecated
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
	@Deprecated
	public class TypeBooklet implements Type {
		/** The name of the document type */
		private static final String name = "booklet";
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public VirtualDocument impose(ModuleData data) throws RenderingException {
			VirtualDocument source = data.asSingleDocument();
			if (preprocess != null) {
				logger.info("preprocess_start");
				Preprocessor preprocessor = new Preprocessor(source, preprocess);
				source = preprocessor.processAll();
			} else {
				logger.verbose("preprocess_skip");
			}
			logger.info("Imposing booklet...");
//			Booklet booklet = Booklet.from(source, binding, flipVerso);
//			Volume volume = booklet.volume();
//			SourceProvider<Page> sp = new SequentialSourceProvider(source);
//			sp.setSourceTo(volume.pages());
//			VirtualDocument imposed = volume.renderDocument();
//			return imposed;
			throw new UnsupportedOperationException("Not implemented anymore");
		}
	}
	
	/**
	 * An n-up document.
	 */
	@Deprecated
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
			int pages = Imposition.this.pages;
			
			NUp nup = new NUp();
			nup.setRows(rows);
			nup.setCols(columns);
			if (pages > 0) {
//				nup.setNumberOfPages(pages);
			}
			VirtualDocument imposed = nup.imposeAsDocument(source);
			return imposed;
		}
	}
	
	/**
	 * A layered document (several documents whose corresponding pages are overlaid).
	 */
	@Deprecated
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
//			logger.info("Imposing overlay...");
//			List<VirtualDocument> docs = data.asMultipleDocuments();
//			Overlay model = Overlay.from(docs);
//			VirtualDocument imposed = model.getDocument();
//			return imposed;
			throw new UnsupportedOperationException("Not implemented anymore");
		}
	}
}
