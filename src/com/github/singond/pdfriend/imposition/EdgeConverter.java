package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.IStringConverter;

/** Converts a string into a valule of the "binding" field. */
public class EdgeConverter implements IStringConverter<Edge> {
	@Override
	public Edge convert(String arg) {
		return Edge.valueOf(arg.toUpperCase());
	}
}