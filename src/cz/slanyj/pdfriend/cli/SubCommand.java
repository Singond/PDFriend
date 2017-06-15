package cz.slanyj.pdfriend.cli;

/**
 * A sub-command of pdfriend, referring to one of its modules
 */
public interface SubCommand {

	/** Execute the subcommand with the given subcommand arguments */
	public void execute(String[] args);
}
