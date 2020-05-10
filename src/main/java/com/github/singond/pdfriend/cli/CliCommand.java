package com.github.singond.pdfriend.cli;

import java.nio.file.Path;
import java.util.List;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import com.github.singond.pdfriend.modules.Module;

public abstract class CliCommand {

	@Parameters
	private List<Path> inputFiles;

	@Option(names={"-o", "--output"})
	private Path outputFile;

	public List<Path> inputFiles() {
		return inputFiles;
	}

	public Path outputFile() {
		return outputFile;
	}

	/**
	 * Returns a fully initialized module ready to perform
	 * the task represented by this subcommand.
	 */
	public abstract Module getModule();
}
