package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.BookletBindingConverter;
import com.github.singond.pdfriend.cli.IntegerDimensionsConverter;
import com.github.singond.pdfriend.cli.PageOptions;
import com.github.singond.pdfriend.cli.ParameterDelegate;
import com.github.singond.pdfriend.cli.SubCommand;
import com.github.singond.pdfriend.geometry.IntegerDimensions;
import com.github.singond.pdfriend.modules.Module;

/**
 * A command-line interface for the imposition module.
 * This handles the {@code pdfriend impose} command.
 * @author Singon
 *
 */
@Parameters(separators="=",
		commandDescription="Lay out pages of the source documents onto pages of a new document")
public class ImposeCommand extends SubCommand {
	@SuppressWarnings("unused")
	private static ExtendedLogger logger = Log.logger(ImposeCommand.class);

	/** A pre-defined type of imposition: booklet, n-up etc. */
	@ParametersDelegate
	private TypeArgument type = new TypeArgument();
	
	/** Specifies where the binding is located */
	@Parameter(names="--binding", converter=BookletBindingConverter.class)
	private Booklet.Binding binding = Booklet.Binding.LEFT;
	
	/** In a vertical booklet, print the verso upside down. */
	@Parameter(names="--verso-opposite")
	private boolean flipVerso = false;
	
	/** Page pre-processing settings */
	@ParametersDelegate
	private PageOptions pageOpts = new PageOptions();
	
	@Parameter(names="--pages", description="")
	private int pages = -1;

	@Override
	public ImposeCommand newInstance() {
		return new ImposeCommand();
	}
	
	@Override
	protected void postParseSpecific() {
		type.postParse();
	}
	
	@Override
	public Module getModule() {
		Imposition impose = new Imposition();
		impose.setBinding(binding);
		impose.setFlipVerso(flipVerso);
		impose.setPages(pages);
		if (pageOpts.isSet()) {
			impose.setPreprocessing(pageOpts.getPreprocessorSettings());
		}
		Imposable task = type.getImpositionTask(impose);
		impose.setTask(task);
		return impose;
	}
	
	/**
	 * Groups the individual imposition types into one group, from which
	 * only one imposition type should be selected.
	 */
	@Parameters(separators=" ")
	public class TypeArgument implements ParameterDelegate {
		@Parameter(names="--booklet", description="A simple stack of sheets folded in half")
		private boolean booklet = false;
		
		@Parameter(names={"--n-up", "--nup"},
				description="Several pages arranged into a grid on a larget sheet",
				converter=IntegerDimensionsConverter.class)
		private IntegerDimensions nup = null;
		
		@Parameter(names="--overlay", description="Print pages on top of each other")
		private boolean overlay = false;
		
		/**
		 * Resolves the type of imposed document from the command line
		 * arguments, creates an instance of an implementing class
		 * and passes necessary parameters to it.
		 * <p>
		 * If more than one type is given in the command line, only one will
		 * be used.
		 * <p>
		 * TODO: It is a good idea to display some kind warning to the user
		 * in case that more types are specified in the command line (eg.
		 * {@code --nup 2x4 --booklet}). Implement the check that exactly one
		 * is selected and display a warning otherwise.
		 * </p>
		 */
		public Imposable getImpositionTask(Imposition module) {
			if (booklet) {
				Imposition.TypeBooklet impl = module.new TypeBooklet();
//				module.setType(impl);
				throw new UnsupportedOperationException("Not implemented yet");
			} else if (nup != null) {
				NUp task = new NUp();
				task.setRows(nup.getFirstDimension());
				task.setCols(nup.getSecondDimension());
				return task;
			} else if (overlay) {
				// TODO Pass some value into layers argument or remove it
				Imposition.TypeOverlay impl = module.new TypeOverlay(-1);
//				module.setType(impl);
				throw new UnsupportedOperationException("Not implemented yet");
			} else {
				throw new IllegalStateException("No imposition type has been set");
			}
		}

		@Override
		public void postParse() {}
	}
}
