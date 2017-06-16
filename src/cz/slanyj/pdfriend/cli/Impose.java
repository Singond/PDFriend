package cz.slanyj.pdfriend.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import cz.slanyj.pdfriend.ExtendedLogger;
import cz.slanyj.pdfriend.Log;
import cz.slanyj.pdfriend.book.control.SequentialSourceProvider;
import cz.slanyj.pdfriend.book.control.SourceProvider;
import cz.slanyj.pdfriend.book.model.Volume;
import cz.slanyj.pdfriend.document.ImportException;
import cz.slanyj.pdfriend.document.RenderingException;
import cz.slanyj.pdfriend.document.VirtualDocument;
import cz.slanyj.pdfriend.format.process.PDFImporter;
import cz.slanyj.pdfriend.format.process.PDFRenderer;
import cz.slanyj.pdfriend.imposition.Booklet;

/**
 * The impose command of pdfriend.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Lay out pages of the source documents onto pages of a new document")
public class Impose implements SubCommand {
	private static final ExtendedLogger logger = Log.logger(Impose.class);

	@ParametersDelegate
	private TypeArgument type = new TypeArgument();
	
	@Parameter(names="--binding", converter=BookletBindingConverter.class)
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	
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
	public void execute(String[] args) {
		logger.info("PDFriend Impose");
		logger.debug("Module arguments: " + Arrays.toString(args));
		for (File f : inputFiles) {
			logger.verbose("Input file: " + f.getAbsolutePath());
		}
		logger.verbose("Output file: "+outputFile.getAbsolutePath());
		
		logger.verbose("Selected imposition type is: " + type.getType().getName());
		type.getType().invokeActionIn(this);
	}
	
	private void imposeBooklet() {
		logger.info("Imposing booklet...");
		// TODO Use all the input files
		File sourceFile = inputFiles.get(0);
		File targetFile = outputFile;
		VirtualDocument source;
		try {
			source = new PDFImporter(sourceFile).importDocument();
			Booklet booklet = Booklet.from(source, binding);
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
	
	/**
	 * Groups the individual imposition types into one group, from which
	 * only one imposition type should be selected.
	 */
	public static class TypeArgument {
		@Parameter(names="--booklet", description="A simple stack of sheets folded in half")
		private boolean booklet = false;
		
		/**
		 * Gets the selected type.
		 * If more than one type is selected, this should throw an exception.
		 * TODO Implement the check that exactly one is selected.
		 * @return
		 */
		public Type getType() {
			if (booklet) return Type.BOOKLET;
			return null;
		}
	}
	
	/**
	 * The type of document to be produced by imposition (a booklet, n-up etc).
	 */
	private enum Type {
		BOOKLET("booklet", i->i.imposeBooklet());
		
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
	
	/** Converts a string into a valule of the "binding" fiíeld. */
	private static class BookletBindingConverter implements IStringConverter<Booklet.Binding> {
		@Override
		public Booklet.Binding convert(String arg) {
			return Booklet.Binding.valueOf(arg.toUpperCase());
		}
	}
}
