package com.github.singond.pdfriend.modules;

import java.io.File;
import java.io.IOException;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFRenderer;
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
	/** The output file. */
	private File outputFile;
	
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

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public void process(VirtualDocument document) {
		logger.info("*** PDFriend Impose ***");
		if (type == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Output file: "+outputFile.getAbsolutePath());
		
		logger.verbose("Selected imposition type is: " + type.getName());
		try {
			type.impose(document);
		} catch (RenderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void imposeBooklet(VirtualDocument source)
			throws RenderingException, IOException {
		logger.info("Imposing booklet...");
		File targetFile = outputFile;
		
		Booklet booklet = Booklet.from(source, binding, flipVerso);
		Volume volume = booklet.volume();
		SourceProvider sp = new SequentialSourceProvider(source);
		sp.setSourceTo(volume.pages());
		VirtualDocument doc = volume.renderDocument();
		new PDFRenderer().renderAndSave(doc, targetFile);
	}
	
	private void imposeNUp(VirtualDocument source, int rows, int columns)
			throws RenderingException, IOException {
		logger.info("Imposing n-up...");
		int pages = this.pages;
		File targetFile = outputFile;
		
		NUp.Builder nup = new NUp.Builder();
		nup.setRows(rows);
		nup.setCols(columns);
		if (pages > 0) {
			nup.setNumberOfPages(pages);
		}
		VirtualDocument imposed = nup.buildFor(source).getDocument();
		new PDFRenderer().renderAndSave(imposed, targetFile);
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
		 */
		public void impose(VirtualDocument doc)
				throws RenderingException, IOException;
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
		public void impose(VirtualDocument doc)
				throws RenderingException, IOException {
			imposeBooklet(doc);
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
		public void impose(VirtualDocument doc)
				throws RenderingException, IOException {
			imposeNUp(doc, rows, columns);
		}
	}
}
