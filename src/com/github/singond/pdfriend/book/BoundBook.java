package com.github.singond.pdfriend.book;

import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * A bouond book (a codex) consisting of a single volume.
 *
 * @author Singon
 *
 */
public class BoundBook implements Book {

	/** The sole volume of this book */
	private final Volume volume;

	/**
	 * Constructs a new {@code BoundBook} object consisting of the given
	 * volume.
	 * @param volume the volume comprising this book
	 */
	public BoundBook(Volume volume) {
		super();
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}

	@Override
	public VirtualDocument renderDocument() {
		return volume.renderDocument();
	}
}
