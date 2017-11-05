package com.github.singond.pdfriend.geometry;

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
	
	@Override
	public String toString() {
		return first + "x" + second;
	}
}