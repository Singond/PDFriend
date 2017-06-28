package com.github.singond.pdfriend.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.cli.parsing.BookletBindingConverter;
import com.github.singond.pdfriend.cli.parsing.IntegerDimensionsConverter;
import com.github.singond.pdfriend.geometry.IntegerDimensions;
import com.github.singond.pdfriend.imposition.Booklet;
import com.github.singond.pdfriend.modules.Impose;
import com.github.singond.pdfriend.modules.Module;

/**
 * The impose command of pdfriend.
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
	
	@Parameter(names="pages", description="")
	private int pages = -1;

	@Override
	public ImposeCommand newInstance() {
		return new ImposeCommand();
	}
	
	@Override
	public void postParse() {}
	
	@Override
	public Module getModule() {
		Impose impose = new Impose();
		impose.setBinding(binding);
		impose.setFlipVerso(flipVerso);
		impose.setPages(pages);
		type.passToModule(impose);
		return impose;
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
		 * Resolves the type of imposed document from the command line
		 * arguments and creates an instance of an implementing class
		 * in the given module instance.
		 * If more than one type is given in the command line, only one will
		 * be used.
		 * <p>
		 * TODO It is a good idea to display some kind warning to the user
		 * in case that more types are specified in the command line (eg.
		 * {@code --nup 2x4 --booklet}. Implement the check that exactly one
		 * is selected and display a warning otherwise.
		 * </p>
		 */
		public void passToModule(Impose module) {
			if (booklet) {
				Impose.TypeBooklet impl = module.new TypeBooklet();
				module.setType(impl);
			} else if (nup != null) {
				Impose.TypeNUp impl = module.new TypeNUp
						(nup.getFirstDimension(), nup.getSecondDimension());
				module.setType(impl);
			} else {
				throw new IllegalStateException("No imposition type has been set");
			}
		}
	}
}
