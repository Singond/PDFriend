package com.github.singond.pdfriend.reorder;

import com.github.singond.pdfriend.cli.ParameterDelegate;

/**
 * A command-line interface for an {@code Reorderable} type.
 * 
 * Each implementation contains the argument for the imposable type itself
 * as well as any type-specific arguments the imposable type may have.
 *
 * @author Singon
 * @param <T> the type of {@code Reorderable} handled by this CLI
 */
interface ReorderableCli<T extends Reorderable> extends ParameterDelegate {

	/**
	 * Checks whether this imposable type has been set in the command line.
	 * @return {@code true} if this command has been given in the command line
	 */
	public boolean isSet();
	
	/**
	 * Returns the object implementing {@code Imposable}, initialized with the
	 * arguments specific to this type.
	 */
	public T getReorderable();
}
