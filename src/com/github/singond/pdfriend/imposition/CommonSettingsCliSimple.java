package com.github.singond.pdfriend.imposition;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.validators.PositiveInteger;
import com.github.singond.pdfriend.cli.ParameterConsistencyException;
import com.github.singond.pdfriend.cli.DimensionsConverter;
import com.github.singond.pdfriend.cli.MarginsConverter;
import com.github.singond.pdfriend.cli.ParameterDelegate;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.CommonSettings.MarginSettings;

/**
 * A command-line interface for {@link CommonSettings}.
 * This variant has less options and it is intended for use by the
 * simple transformation module.
 *
 * Provides configuration settings for the imposition module which affect
 * the output document. The settings include page size, sheet size, number
 * of pages etc.
 * 
 * @author Singon
 */
@Parameters(resourceBundle="Help", separators="=")
class CommonSettingsCliSimple implements ParameterDelegate {

	/** Number of pages in the output document */
	@Parameter(names = "--pages",
	           description = "Number of pages in the output document",
	           descriptionKey = "param-pageCount",
	           validateWith = PositiveInteger.class)
	private int pages = -1;
	
	/** How many times to repeat each page */
	@Parameter(names = "--repeat-page",
	           description = "How many times to repeat each page",
	           descriptionKey = "param-repeatPage",
	           validateWith = PositiveInteger.class)
	private int repeatPage = 1;
	
	/** How many times to repeat each document */
	@Parameter(names = "--repeat-doc",
	           description = "How many times to repeat each document",
	           descriptionKey = "param-repeatDoc",
	           validateWith = PositiveInteger.class)
	private int repeatDocument = 1;
	
	/**
	 * Size of a single page of the assembled output document.
	 * This is the dimensions of the document page in its final form,
	 * ie. in the case of a bound book this would be the format of one
	 * page.
	 */
	@Parameter(names = {"--page-size", "--sheet-size"},
	           description = "Size of a single page of the assembled output document",
	           descriptionKey = "param-pageSize",
	           converter = DimensionsConverter.class)
	private Dimensions pageSize = null;
	
	/**
	 * Interprets paper formats as landscape.
	 */
	@Parameter(names="--landscape",
	           description="Interpret named paper formats (such as A4) as landscape",
	           descriptionKey="param-landscape")
	private boolean landscape = false;
	
	/**
	 * Interprets paper formats as portrait.
	 */
	@Parameter(names="--portrait",
	           description="Interpret named paper formats (such as A4) as portrait",
	           descriptionKey="param-portrait")
	private boolean portrait = false;
	
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
	private Margins margins = null;
	
//	@Parameter(names="--mirror-margins",
//	           arity=1,
//	           description="Whether verso margins should be mirrored",
//	           descriptionKey="param-marginsMirrored")
//	private boolean marginsMirrored = true;

	@Override
	public void postParse() throws ParameterConsistencyException {
		if (landscape && portrait) {
			throw new ParameterConsistencyException(
					"Cannot set both landscape and portrait orientation");
		}
	}
	
	public boolean isSet() {
		return pages > 0
				|| pageSize != null
//				|| sheetSize != null
				|| margins != null;
	}
	
	public CommonSettings getCommonSettings() {
		boolean isLandscape = landscape;
		
		CommonSettings.Builder sb = new CommonSettings.Builder();
		sb.setPageCount(pages);
		sb.setRepeatPage(repeatPage);
		sb.setRepeatDocument(repeatDocument);
		sb.setPageSize(dimSettings(flipFormat(pageSize, isLandscape)));
		sb.setMargins(margins == null ? MarginSettings.AUTO : MarginSettings.of(margins));
//		sb.setMirrorMargins(marginsMirrored);
		sb.setMirrorMargins(false);
		return sb.build();
	}
	
	private Dimensions flipFormat(Dimensions dims, boolean flip) {
		if (flip) {
			return dims.changeOrientation();
		} else {
			return dims;
		}
	}
	
	private DimensionSettings dimSettings(Dimensions dims) {
		if (dims == null) {
			return DimensionSettings.AUTO;
		} else {
			return DimensionSettings.of(dims);
		}
	}
}
