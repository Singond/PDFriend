package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.cli.ParameterDelegate;

/**
 * A command-line interface for an {@code Imposable} type.
 * 
 * Each implementation contains the argument for the imposable type itself
 * as well as any type-specific arguments the imposable type may have.
 *
 * @author Singon
 *
 */
interface ImposableCli extends ParameterDelegate {

	/**
	 * Checks whether this imposable type has been set in the command line.
	 * @return {@code true} if this command has been given in the command line
	 */
	public boolean isSet();
	
	/**
	 * Returns an {@code Imposable} object initialized with the arguments
	 * specific to this type.
	 */
	public Imposable getImposable();
}
