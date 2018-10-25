package com.github.singond.pdfriend.format.process;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDFSettings {

	/**
	 * Returns the PDF box which governs the placement of the PDF page in the
	 * imposition module. Currently, this is the Media Box.
	 * @param page the PDF page whose governing box is to be obtained
	 * @return a PDRectangle representing the governing box
	 */
	static final PDRectangle getBox(PDPage page) {
		return page.getCropBox();
	}
}
