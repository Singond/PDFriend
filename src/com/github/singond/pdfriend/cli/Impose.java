package com.github.singond.pdfriend.cli;

import java.io.File;
import java.io.IOException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.cli.parsing.BookletBindingConverter;
import com.github.singond.pdfriend.cli.parsing.InputFiles;
import com.github.singond.pdfriend.cli.parsing.IntegerDimensions;
import com.github.singond.pdfriend.cli.parsing.IntegerDimensionsConverter;
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
public class Impose extends SubCommand implements Module {
	private static ExtendedLogger logger = Log.logger(Impose.class);

	/** A pre-defined type of imposition: booklet, n-up etc. */
	@ParametersDelegate
	private TypeArgument type = new TypeArgument();
	
	/** Specifies where the binding is located */
	@Parameter(names="--binding", converter=BookletBindingConverter.class)
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	
	/** In a vertical booklet, print the verso upside down. */
	@Parameter(names="--verso-opposite")
	private boolean flipVerso = false;
	
	@Parameter(names="pages", description="")
	private int pages = -1;
	
	/**
	 * The input files.
	 * All files in the list are taken as the input files, and concatenated
	 * in the order they appear in the command.
	 */
//	@ParametersDelegate
//	private InputFiles inputFiles = new InputFiles();
	
	/** The output file. */
	@Parameter(names={"-o", "--output"}, description="Output file name")
	private File outputFile;

	@Override
	public void postParse() {
//		inputFiles.postParse();
	}
	
	@Override
	public void process(VirtualDocument document) {
		logger.info("*** PDFriend Impose ***");
		if (type == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Output file: "+outputFile.getAbsolutePath());
		
		logger.verbose("Selected imposition type is: " + type.getType().getName());
		try {
			type.getType().impose(document);
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
	
	private void imposeNUp(VirtualDocument source)
			throws RenderingException, IOException {
		logger.info("Imposing n-up...");
		int rows = type.nup.getFirstDimension();
		int columns = type.nup.getSecondDimension();
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
	 * Groups the individual imposition types into one group, from which
	 * only one imposition type should be selected.
	 */
	@Parameters(separators=" ")
	public class TypeArgument {
		@Parameter(names="--booklet", description="A simple stack of sheets folded in half")
		private boolean booklet = false;
		
		@Parameter(names={"--n-up", "--nup"},
				description="Several pages arranged into a grid on a larget sheet",
				converter=IntegerDimensionsConverter.class)
		private IntegerDimensions nup = null;
		
		/**
		 * Gets the selected type.
		 * If more than one type is selected, this should throw an exception.
		 * TODO Implement the check that exactly one is selected.
		 * @return an object with the implementation of the concrete
		 *         imposition type
		 */
		public Type getType() {
			if (booklet)
				return new TypeBooklet();
			else if (nup != null)
				return new TypeNUp();
			return null;
		}
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 */
	private interface Type {
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
	private class TypeBooklet implements Type {
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
	private class TypeNUp implements Type {
		/** The name of the document type */
		private static final String name = "n-up";
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public void impose(VirtualDocument doc)
				throws RenderingException, IOException {
			imposeNUp(doc);
		}
	}
}
