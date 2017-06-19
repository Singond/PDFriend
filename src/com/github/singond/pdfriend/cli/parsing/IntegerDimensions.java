package com.github.singond.pdfriend.cli.parsing;

/** A pair of integer dimensions */
public class IntegerDimensions {
	private final int first;
	private final int second;
	
	public IntegerDimensions(int first, int second) {
		this.first = first;
		this.second = second;
	}

	public int getFirstDimension() {
		return first;
	}

	public int getSecondDimension() {
		return second;
	}
}