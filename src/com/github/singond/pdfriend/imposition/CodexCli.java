package com.github.singond.pdfriend.imposition;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.imposition.Codex.Builder;

/**
 * A command-line interface for the "codex" imposable type ({@link Codex}).
 *
 * @author Singon
 *
 */
@Parameters(resourceBundle="Help", separators="=")
class CodexCli implements ImposableCli<Codex.Builder> {

	/**
	 * The list of sheet stack manipulations which describes the book block.
	 * If this list is not null, then book imposition has been selected.
	 * 
	 * The manipulations are to be listed in the order they are performed
	 * starting with the unfolded sheet.
	 */
	@Parameter(names={"--book", "--codex"},
	           descriptionKey="book-command",
	           description="A bound book of several signatures",
	           listConverter=ManipulationsConverter.class)
	private List<ManipulationProxy> manipulations = null;

	@Parameter(names={"--stack", "--sheets-per-signature"},
	           descriptionKey="book-stack",
	           description="Number of sheets per signature")
	// TODO Add validator
	private int sheetsPerSignature = 1;
	
	@Override
	public void postParse() throws ParameterConsistencyException {
		// Do nothing
	}

	@Override
	public boolean isSet() {
		return manipulations != null;
	}

	@Override
	public Codex.Builder getImposable() {
		Codex.Builder codex = Codex.rightBuilder();
		codex.setSheetsInSignature(sheetsPerSignature);
		for (ManipulationProxy m : manipulations) {
			m.applyTo(codex);
		}
		return codex;
	}
	
	private static abstract class ManipulationProxy {
		/** The code identifying this manipulation in the command line */
		/*private final String code;
		
		ManipulationProxy(String code) {
			this.code = code;
		}*/
		
		/** Applies the manipulation to the given codex builder object. */
		abstract void applyTo(Codex.Builder codex);
	}
	
	/** Represents a fold along horizontal axis */
	private static class HorizontalFoldProxy extends ManipulationProxy {
		private final boolean up;
		
		private HorizontalFoldProxy(boolean up) {
			this.up = up;
		}
		
		static HorizontalFoldProxy foldUp() {
			return new HorizontalFoldProxy(true);
		}
		
		static HorizontalFoldProxy foldDown() {
			return new HorizontalFoldProxy(false);
		}

		@Override
		void applyTo(Builder codex) {
			if (up) {
				codex.foldHorizontallyUp();
			} else {
				codex.foldHorizontallyDown();
			}
		}
	}
	
	/** Represents a fold along vertical axis */
	private static class VerticalFoldProxy extends ManipulationProxy {
		private final boolean up;
		
		private VerticalFoldProxy(boolean up) {
			this.up = up;
		}
		
		static VerticalFoldProxy foldUp() {
			return new VerticalFoldProxy(true);
		}
		
		static VerticalFoldProxy foldDown() {
			return new VerticalFoldProxy(false);
		}

		@Override
		void applyTo(Builder codex) {
			if (up) {
				codex.foldVerticallyUp();
			} else {
				codex.foldVerticallyDown();
			}
		}
	}
	
	/**
	 * Parses a string into a representation of a sheet stack manipulation.
	 * <ul>
	 * <li>{@code H}: Fold the stack along a horizontal line
	 * <li>{@code V}: Fold the stack along a vertical line
	 * <li>Minus ({@code -}) after a command means that the folded part
	 * of the page is placed to the front of the sheet (closer to the
	 * observer).
	 * This convention has been chosed so that the reference corner stays
	 * in the front.
	 * 
	 * @param arg the string to be parsed
	 * @return an object which is able to set the correct manipulation object
	 *         to a given {@code Codex.Builder} instance
	 */
	private static ManipulationProxy parseManipulationProxy(String arg) {
		if (arg.startsWith("H-")) {
			return HorizontalFoldProxy.foldUp();
		} else if (arg.startsWith("H")) {
			return HorizontalFoldProxy.foldDown();
		} else if (arg.startsWith("V-")) {
			return VerticalFoldProxy.foldUp();
		} else if (arg.startsWith("V")) {
			return VerticalFoldProxy.foldDown();
		} else {
			throw new IllegalArgumentException("Unknown sheet manipulation argument: " + arg);
		}
	}
	
	static class ManipulationsConverter implements IStringConverter<List<ManipulationProxy>> {

		/**
		 * @throws ParameterException if the argument cannot be parsed
		 */
		@Override
		public List<ManipulationProxy> convert(String argLine) {
			String[] args = argLine.split(",");
			List<ManipulationProxy> result = new ArrayList<>(args.length);
			
			try {
				for (String arg : args) {
					result.add(parseManipulationProxy(arg));
				}
			} catch (IllegalArgumentException e) {
				throw new ParameterException(e.getMessage(), e);
			}
			return result;
		}
	}
}
