package com.github.singond.pdfriend.cli;

import com.beust.jcommander.IStringConverter;
import com.github.singond.pdfriend.imposition.Booklet;

/** Converts a string into a valule of the "binding" field. */
@Deprecated
public class BookletBindingConverter implements IStringConverter<Booklet.Binding> {
	@Override
	public Booklet.Binding convert(String arg) {
		return Booklet.Binding.valueOf(arg.toUpperCase());
	}
}