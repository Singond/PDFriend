package com.github.singond.pdfriend.book.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.control.BookUtils;
import com.github.singond.pdfriend.document.VirtualDocument;

/**
 * The whole text block of a document, made by arranging several Signatures
 * in a sequence.
 * @author Singon
 *
 */
public class Volume {

	/**
	 * Signatures sorted in their proper order in the finished volume.
	 */
	private List<Signature> signatures;
	
	private static ExtendedLogger logger = Log.logger(Volume.class);
	
	public Volume() {
		signatures = SetUniqueList.setUniqueList(new LinkedList<Signature>());
	}
	
	/**
	 * Inserts the given Signature into this Volume.
	 * @param signature
	 */
	public boolean add(Signature signature) {
		return signatures.add(signature);
	}
	
	/**
	 * Iterates through the Leaves in this Volume in the order of the
	 * Signatures, and on the level of Signature in the current order
	 * in that Signature.
	 * @return A new Iterator object starting at the first Leaf.
	 */
	public Iterator<Leaf> leafIterator() {
		return new Iterator<Leaf>() {
			/** Iterator through Signatures, will not change. */
			private final Iterator<Signature> signatureIterator = signatures.iterator();
			/**
			 * Iterator through Pages of a Signature. Will be new for every Page.
			 */
			private Iterator<Leaf> leafIterator;
			/**
			 * Signifies whether hasNext() has been called since the last call to next().
			 */
			private boolean nextReady = false;

			@Override
			public boolean hasNext() {
				nextReady = true;
				if (leafIterator != null && leafIterator.hasNext()) { // The null check is for initialization
					return true;
				} else if (signatureIterator.hasNext()) {
					leafIterator = signatureIterator.next().leafIterator();
					return hasNext();
				} else {
					return false;
				}
			}

			@Override
			public Leaf next() {
				if (!nextReady) {
					hasNext();
				}
				return leafIterator.next();
			}
		};
	}
	
	/**
	 * Wraps this object to iterate through the Leaves in the order
	 * specified in leafIterator().
	 * @see {@link #leafIterator}
	 * @return This object wrapped as an Iterable<Leaf>.
	 */
	public Iterable<Leaf> leaves() {
		return new Iterable<Leaf>() {
			@Override
			public Iterator<Leaf> iterator() {
				return leafIterator();
			}
		};
	}
	
	/**
	 * Wraps this object to iterate through the pages in the order of the
	 * Leaves and with the recto of each Leaf coming right before its verso.
	 * @see {@link #leafIterator}
	 * @return This object wrapped as an Iterable<Page>.
	 */
	public Iterable<Page> pages() {
		return new Iterable<Page>() {
			@Override
			public Iterator<Page> iterator() {
				return BookUtils.pageIterator(leafIterator());
			}
		};
	}
	
	/**
	 * Renders this Volume as a new VirtualDocument.
	 * @return a new VirtualDocument instance
	 */
	public VirtualDocument renderDocument() {
		logger.info("volume_rendering", this);
		VirtualDocument.Builder document = new VirtualDocument.Builder();
		for (Signature s : signatures) {
			s.renderAllSheets(document);
		}
		return document.build();
	}
	
	@Override
	public String toString() {
		return "Volume@" + hashCode();
	}
}
