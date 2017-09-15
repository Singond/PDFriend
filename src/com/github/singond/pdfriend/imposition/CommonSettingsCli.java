package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.github.singond.pdfriend.cli.ArgumentParsingException;
import com.github.singond.pdfriend.cli.DimensionsConverter;
import com.github.singond.pdfriend.cli.MarginsConverter;
import com.github.singond.pdfriend.cli.ParameterDelegate;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Margins;

/**
 * A command-line interface for {@link CommonSettings}.
 *
 * Provides configuration settings for the imposition module which affect
 * the output document. The settings include page size, sheet size, number
 * of pages etc.
 * 
 * @author Singon
 */
public class CommonSettingsCli implements ParameterDelegate {

	/** Number of pages in the output document */
	@Parameter(names="--pages",
	           description="Number of pages in the output document",
	           descriptionKey="param-pageCount")
	private int pages = -1;
	
	/**
	 * Size of a single page of the assembled output document.
	 * This is the dimensions of the document page in its final form,
	 * ie. in the case of a bound book this would be the format of one
	 * page.
	 */
	@Parameter(names="--page-size",
	           description="Size of a single page of the assembled output document",
	           descriptionKey="param-pageSize",
	           converter=DimensionsConverter.class)
	private Dimensions pageSize = CommonSettings.AUTO_DIMENSIONS;
	
	/**
	 * Size of the output sheet before assembling the document.
	 * This is the dimensions of the output document in its raw form,
	 * ie. the format of pages the user will see on computer screen
	 * and which will be printed.
	 * In the case of a bound book, this would be the format of the
	 * printer's paper sheet upon which individual pages are imposed.
	 */
	@Parameter(names="--sheet-size",
	           description="Size of a single page of the assembled output document",
	           descriptionKey="param-pageSize",
	           converter=DimensionsConverter.class)
	private Dimensions sheetSize = CommonSettings.AUTO_DIMENSIONS;
	
	/**
	 * Margins of the output page.
	 * This argument takes one of the following forms:
	 * <li>A single length like {@code A}: All four margins have the specified
	 *     width, ie. left = right = top = bottom = A.
	 * <li>Two lengths like {@code A,B}: These are taken to mean the horizontal
	 *     and vertical margins, respectively. This means left = right = A,
	 *     bottom = top = B.
	 * <li>Four lengths like {@code A,B,C,D}. Each margin can have its own
	 *     width. The numbers are interpreted as left, right, bottom and top
	 *     margin, respectively: left = A, right = B, bottom = C, top = D.
	 */
	@Parameter(names="--margins",
	           description="Margins of the output page",
	           descriptionKey="param-margins",
	           converter=MarginsConverter.class)
	private Margins margins = CommonSettings.AUTO_MARGINS;

	@Override
	public void postParse() throws ArgumentParsingException {
		// Do nothing
	}
	
	public boolean isSet() {
		return pages > 0
				|| pageSize != CommonSettings.AUTO_DIMENSIONS
				|| sheetSize != CommonSettings.AUTO_DIMENSIONS
				|| margins != CommonSettings.AUTO_MARGINS;
	}
	
	public CommonSettings getCommonSettings() {
		CommonSettings.Builder sb = new CommonSettings.Builder();
		sb.setPages(pages);
		sb.setPageSize(pageSize);
		sb.setSheetSize(sheetSize);
		sb.setMargins(margins);
		return sb.build();
	}
}
