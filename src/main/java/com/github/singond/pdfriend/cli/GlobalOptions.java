package com.github.singond.pdfriend.cli;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;

public class GlobalOptions {

	@ArgGroup(exclusive = true)
	private VerbosityOptions verbosity;

	public int verbosity() {
		if (verbosity != null) {
			return verbosity.verbose.length - verbosity.quiet.length;
		} else {
			return 0;
		}
	}

	// TODO: The -v -q exclusion does not work.
	public static class VerbosityOptions {

		@Option(names={"-v", "--verbose"}, description="Increase verbosity level")
		private boolean[] verbose = new boolean[0];

		@Option(names={"-q", "--quiet"}, description="Decrease verbosity level")
		private boolean[] quiet = new boolean[0];
	}
}
