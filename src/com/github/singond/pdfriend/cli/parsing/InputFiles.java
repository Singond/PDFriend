package com.github.singond.pdfriend.cli.parsing;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.format.ImportException;
import com.github.singond.pdfriend.format.process.PDFImporter;
//import com.github.singond.pdfriend.io.FileInput;
import com.github.singond.pdfriend.io.Input;
import com.github.singond.pdfriend.io.InputFactory;

/**
 * Collects the unnamed arguments from the command line and builds a list
 * of input files based on these arguments.
 * @author Singon
 */
public class InputFiles {

	private static ExtendedLogger logger = Log.logger(InputFiles.class);
	
	@Parameter(description="The list of input files")
	private List<File> files = new ArrayList<>();
	
	public void postParse() {
		logger.verbose(listFiles(files));
	}
	
	/**
	 * Returns the input files wrapped in a single VirtualDocument.
	 * @return a new virtual document with the first of the input files only
	 * TODO Concatenate all files into one (do not ignore the rest like now)
	 * @throws ImportException 
	 */
	@Deprecated
	public VirtualDocument getAsDocument() throws ImportException {
		// TODO Support other file formats?
//		return new PDFImporter(files.get(0)).importDocument();
		return null;
	}
	
	/**
	 * Returns the input files wrapped in FileInput objects.
	 */
	@Deprecated
//	public List<FileInput> getInputFiles() {
//		List<FileInput> result = files.stream()
//				.map(f->new FileInput(f.toPath()))
//				.collect(Collectors.toList());
//		return result;
//	}
	
	/**
	 * Returns the input files wrapped in one Input object.
	 */
	public Input getInput() {
		List<Path> paths = files.stream()
				.map(f->f.toPath())
				.collect(Collectors.toList());
		return InputFactory.of(paths);
	}
	
	/**
	 * Provides a textual list of the input files received.
	 * @param files the list of files to be printed into string
	 * @return a new String with each file name on new, indented line
	 */
	private String listFiles(List<File> files) {
		StringBuilder out = new StringBuilder("The input files:");
		for (File f : files) {
			out.append("\n\t").append(f.getAbsolutePath());
		}
		return out.toString();
	}
}
