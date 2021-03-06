package com.github.singond.pdfriend.imposition;

import java.util.EnumMap;
import java.util.Map;

import com.github.singond.pdfriend.SpecVal;
import com.github.singond.pdfriend.geometry.Margins;

/**
 * Settings common to the Imposition module.
 * These are various settings for the output document, the exact meaning
 * of which can vary slightly depending on the type of imposition.
 * <p>
 * Instances of this class are immutable.
 *
 * @author Singon
 *
 */
public final class CommonSettings {

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
	private final MarginSettings margins;
	
	
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
	                      MarginSettings margins, boolean mirrorMargins) {
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
				DimensionSettings.AUTO, MarginSettings.AUTO, true);
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

	public MarginSettings getMargins() {
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
				.append(", margins: ").append(margins)
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
		private MarginSettings margins = MarginSettings.AUTO;
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
		public MarginSettings getMargins() {
			return margins;
		}
		public void setMargins(MarginSettings margins) {
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
	
	/**
	 * The number of rows and columns in the grid.
	 */
	static class MarginSettings extends SpecVal<MarginType, Margins> {

		private static final Map<MarginType, MarginSettings> instanceMap = new EnumMap<>(MarginType.class);
		static {
			for (MarginType type : MarginType.values()) {
				instanceMap.put(type, new MarginSettings(type));
			}
		}
		
		public static final MarginSettings AUTO = MarginSettings.of(MarginType.AUTO);
		public static final MarginSettings VALUE = MarginSettings.of(MarginType.VALUE);
		
		private MarginSettings(MarginType type) {
			super(type);
		}
		
		private MarginSettings(Margins value) {
			super(value);
		}
		
		public static MarginSettings of(MarginType type) {
			return instanceMap.get(type);
		}
		
		public static MarginSettings of(Margins value) {
			return new MarginSettings(value);
		}

		@Override
		protected MarginType getValueConstant() {
			return MarginType.VALUE;
		}
	}
	
	static enum MarginType {
		/** Margins are automatic */
		AUTO,
		/** Explicit value of margins */
		VALUE;
	}
}
