package com.github.singond.pdfriend.io;

import java.util.Iterator;
import java.util.List;

import com.github.singond.pdfriend.io.InputElement;

/**
 * A group of input elements to be processed together.
 * @author Singon
 */
class MultiInput implements Input {
	private final Iterator<InputElement> inputIter;
	
	MultiInput(List<InputElement> input) {
		this.inputIter = input.iterator();
	}

	@Override
	public InputElement next() {
		return inputIter.next();
	}

	@Override
	public boolean hasNext() {
		return inputIter.hasNext();
	}
}
