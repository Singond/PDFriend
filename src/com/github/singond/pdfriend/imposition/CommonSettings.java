package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Margins;

/**
 * Settings common to the Imposition module.
 * These are various settings for the output document, the exact meaning
 * of which can vary slightly depending on the type of imposition.
 *
 * @author Singon
 *
 */
public class CommonSettings {
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
	 * Size of a single page of the assembled output document.
	 * This is the dimensions of the document page in its final form,
	 * ie. in the case of a bound book this would be the format of one
	 * page.
	 */
	private final Dimensions pageSize;
	
	/**
	 * Size of the output sheet before assembling the document.
	 * This is the dimensions of the output document in its raw form,
	 * ie. the format of pages the user will see on computer screen
	 * and which will be printed.
	 * In the case of a bound book, this would be the format of the
	 * printer's paper sheet upon which individual pages are imposed.
	 */
	private final Dimensions sheetSize;
	
	/**
	 * Margins of the output page.
	 */
	private final Margins margins;
	
	
	/**
	 * Constructs a new settings object.
	 * @param pageCount
	 * @param pageSize
	 * @param sheetSize
	 * @param margins
	 */
	public CommonSettings(int pageCount, Dimensions pageSize,
	                      Dimensions sheetSize, Margins margins) {
		this.pages = pageCount;
		this.pageSize = pageSize;
		this.sheetSize = sheetSize;
		this.margins = margins;
	}
	
	/**
	 * Constructs a new settings object with all values set to auto.
	 * @return a new {@code CommonSettings} object
	 */
	public static CommonSettings auto() {
		return new CommonSettings(-1, AUTO_DIMENSIONS, AUTO_DIMENSIONS,
		                          AUTO_MARGINS);
	}

	public int getPageCount() {
		return pages;
	}

	public Dimensions getPageSize() {
		return pageSize;
	}

	public Dimensions getSheetSize() {
		return sheetSize;
	}

	public Margins getMargins() {
		return margins;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("pages: ").append(pages)
				.append(", pageSize: ").append(pageSize)
				.append(", sheetSize: ").append(sheetSize)
				.append(", margins: ").append(margins==AUTO_MARGINS ? "AUTO" : margins);
		return builder.toString();
	}

	/**
	 * A builder for {@link CommonSettings} objects.
	 */
	public static class Builder {
		private int pages = -1;
		private Dimensions pageSize = AUTO_DIMENSIONS;
		private Dimensions sheetSize = AUTO_DIMENSIONS;
		private Margins margins = AUTO_MARGINS;
		
		public int getPageCount() {
			return pages;
		}
		public void setPageCount(int pages) {
			this.pages = pages;
		}
		public Dimensions getPageSize() {
			return pageSize;
		}
		public void setPageSize(Dimensions pageSize) {
			this.pageSize = pageSize;
		}
		public Dimensions getSheetSize() {
			return sheetSize;
		}
		public void setSheetSize(Dimensions sheetSize) {
			this.sheetSize = sheetSize;
		}
		public Margins getMargins() {
			return margins;
		}
		public void setMargins(Margins margins) {
			this.margins = margins;
		}
		
		public CommonSettings build() {
			return new CommonSettings(pages, pageSize, sheetSize, margins);
		}
	}
}
