package com.github.singond.pdfriend.cli;

import com.beust.jcommander.Parameter;

public class GlobalOptions {
	/** Print version info and exit */
	@Parameter(names={"-V", "--version"}, description="Print version info and exit", order=0)
	private boolean version = false;
	
	/** Print help info and exit */
	@Parameter(names={"-h", "-?", "--help"}, description="Print this help page and exit", order=1)
	private boolean help = false;
	
	/** Set Log4j to VERBOSE level */
	@Parameter(names={"-v", "--verbose"}, description="Verbose output", order=4)
	private boolean verbose = false;
	
	/** Set Log4j to DEBUG level */
	@Parameter(names={"-vv", "--debug"}, description="Extra verbose output, used for debugging", order=5)
	private boolean debug = false;
	
	/** Set Log4j to WARN level */
	@Parameter(names={"-q", "--quiet"}, description="Be less verbose than normal, display only warnings", order=6)
	private boolean quiet = false;

	/**
	 * Print version info and exit.
	 * @return true if this flag has been set
	 */
	public boolean version() {
		return version;
	}

	/**
	 * Print help page and exit.
	 * @return true if this flag has been set
	 */
	public boolean help() {
		return help;
	}

	/**
	 * Set Log4j to VERBOSE level.
	 * @return true if this flag has been set
	 */
	public boolean verbose() {
		return verbose;
	}

	/**
	 * Set Log4j to DEBUG level.
	 * @return true if this flag has been set
	 */
	public boolean debug() {
		return debug;
	}

	/**
	 * Set Log4j to WARN level.
	 * @return true if this flag has been set
	 */
	public boolean quiet() {
		return quiet;
	}
}
