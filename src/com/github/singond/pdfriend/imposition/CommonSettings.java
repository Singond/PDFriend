package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Margins;

/**
 * Settings common to the Imposition module.
 * These are various settings for the output document, the exact meaning
 * of which can vary slightly depending on the type of imposition.
 * <p>
 * Instances of this class are immutable.
 * NOTE: The immutability is not guaranteed if {@code Margins} is subclassed.
 * TODO: Make {@code Margins} final?
 *
 * @author Singon
 *
 */
public final class CommonSettings {
	@Deprecated
	public static final Dimensions AUTO_DIMENSIONS = Dimensions.dummy();
	public static final Margins AUTO_MARGINS = new Margins(null, null, null, null) {
		@Override
		public String toString() {return "AUTO";}
	};

	/**
	 * Number of pages in the output document.
	 * Negative values are interpreted as a direction to calculate the
	 * page count automatically.
	 */
	private final int pages;
	
	/**
	 * How many times to output each page in a document before proceeding
	 * to the next page.
	 * If this is combined with "repeat document", then each repetition
	 * of the document will have its pages repeated by this number.
	 * <p>
	 * The default is one, zero means "repeat infinitely", negative values
	 * are not allowed.
	 */
	private final int repeatPage;
	
	/**
	 * How many times to output each document in a document before proceeding
	 * to the next page.
	 * If this is combined with "repeat page", then each repetition
	 * of the document will have its pages repeated by the number given
	 * in "repeat page"
	 * <p>
	 * The default is one, zero means "repeat infinitely", negative values
	 * are not allowed.
	 */
	private final int repeatDocument;
	
	/**
	 * Size of a single page of the assembled output document.
	 * This is the dimensions of the document page in its final form,
	 * ie. in the case of a bound book this would be the format of one
	 * page.
	 */
	private final DimensionSettings pageSize;
	
	/**
	 * Size of the output sheet before assembling the document.
	 * This is the dimensions of the output document in its raw form,
	 * ie. the format of pages the user will see on computer screen
	 * and which will be printed.
	 * In the case of a bound book, this would be the format of the
	 * printer's paper sheet upon which individual pages are imposed.
	 */
	private final DimensionSettings sheetSize;
	
	/**
	 * Margins of the output page.
	 */
	private final Margins margins;
	
	
	/**
	 * Margins are mirrored on verso pages.
	 */
	private final boolean mirrorMargins;
	
	/**
	 * Constructs a new settings object.
	 * @param pageCount
	 * @param pageSize
	 * @param sheetSize
	 * @param margins
	 * @param mirrorMargins
	 */
	public CommonSettings(int pageCount, int repeatPage, int repeatDoc,
	                      DimensionSettings pageSize, DimensionSettings sheetSize,
	                      Margins margins, boolean mirrorMargins) {
		this.pages = pageCount;
		this.repeatPage = repeatPage;
		this.repeatDocument = repeatDoc;
		this.pageSize = pageSize;
		this.sheetSize = sheetSize;
		this.margins = margins;
		this.mirrorMargins = mirrorMargins;
	}
	
	/**
	 * Constructs a new settings object with all values set to auto.
	 * @return a new {@code CommonSettings} object
	 */
	public static CommonSettings auto() {
		return new CommonSettings(-1, 1, 1, DimensionSettings.AUTO,
				DimensionSettings.AUTO, AUTO_MARGINS, true);
	}

	public int getPageCount() {
		return pages;
	}
	
	public boolean isAutoPageCount() {
		return pages < 1;
	}

	public int getRepeatPage() {
		return repeatPage;
	}

	public int getRepeatDocument() {
		return repeatDocument;
	}

	public DimensionSettings getPageSize() {
		return pageSize;
	}

	public DimensionSettings getSheetSize() {
		return sheetSize;
	}

	public Margins getMargins() {
		return margins;
	}

	public boolean isMirrorMargins() {
		return mirrorMargins;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("pages: ").append(pages)
				.append(", repeat page: ").append(repeatPage)
				.append(", repeat document: ").append(repeatDocument)
				.append(", page size: ").append(pageSize)
				.append(", sheet size: ").append(sheetSize)
				.append(", margins: ").append(margins==AUTO_MARGINS ? "auto" : margins)
				.append(", margins mirrored: ").append(mirrorMargins);
		return builder.toString();
	}

	/**
	 * A builder for {@link CommonSettings} objects.
	 */
	public static class Builder {
		private int pages = -1;
		private int repeatPage = 1;
		private int repeatDocument = 1;
		private DimensionSettings pageSize = DimensionSettings.AUTO;
		private DimensionSettings sheetSize = DimensionSettings.AUTO;
		private Margins margins = AUTO_MARGINS;
		private boolean mirrorMargins = true;
		
		public int getPageCount() {
			return pages;
		}
		public void setPageCount(int pages) {
			this.pages = pages;
		}
		public int getRepeatPage() {
			return repeatPage;
		}
		public void setRepeatPage(int repeatPage) {
			this.repeatPage = repeatPage;
		}
		public int getRepeatDocument() {
			return repeatDocument;
		}
		public void setRepeatDocument(int repeatDocument) {
			this.repeatDocument = repeatDocument;
		}
		public DimensionSettings getPageSize() {
			return pageSize;
		}
		public void setPageSize(DimensionSettings pageSize) {
			this.pageSize = pageSize;
		}
		public DimensionSettings getSheetSize() {
			return sheetSize;
		}
		public void setSheetSize(DimensionSettings sheetSize) {
			this.sheetSize = sheetSize;
		}
		public Margins getMargins() {
			return margins;
		}
		public void setMargins(Margins margins) {
			this.margins = margins;
		}
		public boolean isMirrorMargins() {
			return mirrorMargins;
		}
		public void setMirrorMargins(boolean mirrorMargins) {
			this.mirrorMargins = mirrorMargins;
		}
		public CommonSettings build() {
			return new CommonSettings(pages, repeatPage, repeatDocument,
					pageSize, sheetSize, margins, mirrorMargins);
		}
	}
}
