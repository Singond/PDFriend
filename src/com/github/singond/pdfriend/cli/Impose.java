package com.github.singond.pdfriend.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.SequentialSourceProvider;
import com.github.singond.pdfriend.book.control.SourceProvider;
import com.github.singond.pdfriend.book.model.Volume;
import com.github.singond.pdfriend.cli.parsing.BookletBindingConverter;
import com.github.singond.pdfriend.cli.parsing.IntegerDimensions;
import com.github.singond.pdfriend.cli.parsing.IntegerDimensionsConverter;
import com.github.singond.pdfriend.document.ImportException;
import com.github.singond.pdfriend.document.RenderingException;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.process.PDFImporter;
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
public class Impose implements SubCommand {
	private static final ExtendedLogger logger = Log.logger(Impose.class);

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
	@Parameter(description="Input files")
	private List<File> inputFiles = new ArrayList<>();
	
	/** The output file. */
	@Parameter(names={"-o", "--output"}, description="Output file name")
	private File outputFile;

	@Override
	public void execute() {
		logger.info("*** PDFriend Impose ***");
		if (type == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		
		for (File f : inputFiles) {
			logger.verbose("Input file: " + f.getAbsolutePath());
		}
		logger.verbose("Output file: "+outputFile.getAbsolutePath());
		
		logger.verbose("Selected imposition type is: " + type.getType().getName());
		type.getType().invokeActionIn(this);
	}
	
	private void imposeBooklet() {
		logger.info("Imposing booklet...");
		File targetFile = outputFile;
		VirtualDocument source;
		try {
			source = getInput(inputFiles);
			Booklet booklet = Booklet.from(source, binding, flipVerso);
			Volume volume = booklet.volume();
			SourceProvider sp = new SequentialSourceProvider(source);
			sp.setSourceTo(volume.pages());
			VirtualDocument doc = volume.renderDocument();
			new PDFRenderer().renderAndSave(doc, targetFile);
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void imposeNUp() {
		logger.info("Imposing n-up...");
		int rows = type.nup.getFirstDimension();
		int columns = type.nup.getSecondDimension();
		int pages = this.pages;
		File targetFile = outputFile;
		
		try {
			VirtualDocument source = getInput(inputFiles);
			NUp.Builder nup = new NUp.Builder();
			nup.setRows(rows);
			nup.setCols(columns);
			if (pages > 0) {
				nup.setNumberOfPages(pages);
			}
			VirtualDocument imposed = nup.buildFor(source).getDocument();
			new PDFRenderer().renderAndSave(imposed, targetFile);
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private VirtualDocument getInput(List<File> files) throws ImportException {
		// TODO Use all the input files
		File sourceFile = inputFiles.get(0);
		return new PDFImporter(sourceFile).importDocument();
	}
	
	/**
	 * Groups the individual imposition types into one group, from which
	 * only one imposition type should be selected.
	 */
	@Parameters(separators=" ")
	public static class TypeArgument {
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
		 * @return
		 */
		public Type getType() {
			if (booklet) return Type.BOOKLET;
			else if (nup != null) return Type.N_UP;
			return null;
		}
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 */
	private enum Type {
		BOOKLET("booklet", i->i.imposeBooklet()),
		N_UP("n-up", i->i.imposeNUp());
		
		/** The name of the type */
		private final String name;
		/** The action to invoke on Impose */
		private final Consumer<Impose> action;
		
		/**
		 * @param name the user-friendly name of the imposition type
		 * @param action the action to invoke in Impose object
		 */
		private Type(String name, Consumer<Impose> action) {
			this.name = name;
			this.action = action;
		}
		
		/** Returns a user-friendly name of the imposition type. */
		public String getName() {
			return name;
		}
		
		/** Invokes the action in the Impose object. */
		public void invokeActionIn(Impose impose) {
			action.accept(impose);
		}
	}
}
