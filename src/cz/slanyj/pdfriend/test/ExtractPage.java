/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.slanyj.pdfriend.test;

import java.awt.geom.AffineTransform;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.multipdf.PDFCloneUtility;

/**
 * Example to show superimposing a PDF page onto another PDF.
 * Copied from org.apache.pdfbox.examples.documentmanipulation.SuperimposePage
 */
public class ExtractPage {

	public static void main(final String[] args) {
		try {

			// Create a new document with some basic content
			PDDocument aDoc = new PDDocument();
			PDPage aPage = new PDPage();
			
			PDFCloneUtility cloner = new PDFCloneUtility(aDoc);

			// get the page crop box. Will be used later to place the
			// imported page.
			PDRectangle cropBox = aPage.getCropBox();

			//aDoc.addPage(aPage);

			PDDocument toBeImported = PDDocument.load(new File(args[0]));

			PDPage page = toBeImported.getPage(0);
			aDoc.addPage(aPage);
			cloner.cloneMerge(page, aPage);

			// close the imported document
			toBeImported.close();

			aDoc.save(args[1]);
			aDoc.close();
		}
		catch (Exception e) {
			//System.out.println(" error creating pdf file." + e.toString());
			e.printStackTrace();
		}
	}
}
