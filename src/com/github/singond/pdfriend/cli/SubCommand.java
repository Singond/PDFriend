package com.github.singond.pdfriend.cli;

/**
 * A sub-command of pdfriend, referring to one of its modules
 */
public interface SubCommand {

	/**
	 * Method to be invoked after the SubCommand object is built
	 * and initialized with values from the command line.
	 */
	public void postParse();
	
	/**
	 * Execute the subcommand. This method does not allow any arguments,
	 * meaning the module must be completely initialized to perform the
	 * intended task in advance. 
	 */
	public void execute();
}
