package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.cli.ParameterDelegate;

/**
 * A command-line interface for an {@code Imposable} type.
 * 
 * Each implementation contains the argument for the imposable type itself
 * as well as any type-specific arguments the imposable type may have.
 *
 * @author Singon
 * @param <T> the type of {@code ImposableBuilder} handled by this CLI
 */
interface ImposableCli<T extends ImposableBuilder<?>> extends ParameterDelegate {

	/**
	 * Checks whether this imposable type has been set in the command line.
	 * @return {@code true} if this command has been given in the command line
	 */
	public boolean isSet();
	
	/**
	 * Returns the object implementing {@code Imposable}, initialized with the
	 * arguments specific to this type.
	 */
	public T getImposable();
}
