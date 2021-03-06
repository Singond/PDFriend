package com.github.singond.pdfriend.imposition;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;
import com.github.singond.pdfriend.format.ParsingException;
import com.github.singond.pdfriend.format.RenderingException;
import com.github.singond.pdfriend.format.process.PDFParser;
import com.github.singond.pdfriend.format.process.PDFRenderer;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.geometry.Length;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.geometry.Margins;
import com.github.singond.pdfriend.imposition.Preprocessor.Resizing;

@SuppressWarnings("unused")
public class PreprocessDocument {

	private static ExtendedLogger logger = Log.logger(PreprocessDocument.class);

	private static final LengthUnit PT = LengthUnits.POINT_POSTSCRIPT;
	private static final LengthUnit MM = LengthUnits.MILLIMETRE;

	public static void main(String[] args) {
		File input1 = new File("test/lorem-letter-bg.pdf");
		File output = new File("test/preprocessed-doc.pdf");

		try {
			/* Input Document */
			@SuppressWarnings("resource")
			VirtualDocument inDoc1 = new PDFParser().parseDocument(Files.newInputStream(input1.toPath()));

			Preprocessor.Settings settings = new Preprocessor.Settings();
//			settings.setScale(1);
//			settings.setRotation(Math.PI/2);
			settings.setRotation(0.1);
			settings.setResizing(Resizing.FIT);
//			settings.setPageDimensions(new Dimensions(306, 396, PT));
//			settings.setPageDimensions(new Dimensions(612, 792, PT));
//			settings.setPageDimensions(new Dimensions(1224, 1584, PT));
			//
//			settings.setCellDimensions(new Dimensions(306, 396, PT));
			settings.setCellDimensions(new Dimensions(612, 792, PT));
//			settings.setCellDimensions(new Dimensions(1224, 1584, PT));
			Margins margins = new Margins(new Length(100, PT),
			                              new Length(100, PT),
			                              new Length(0, PT),
			                              new Length(0, PT));
			settings.setCellMargins(margins);
//			AlignmentSetter align = new AlignmentSetter();
//			align.addAlignment("CenterAlignment", 0);
//			align.addAlignment("MiddleAlignment", 0);
//			align.setAlignment(settings);
			settings.setHorizontalAndVerticalAlignment(0, 0);

//			settings.setCellDimensions(new Dimensions(200, 100, MM));
			Preprocessor pp = new Preprocessor(Arrays.asList(inDoc1), settings);

			/* Output */
			VirtualDocument.Builder outDoc = new VirtualDocument.Builder();
			for (VirtualPage pg : inDoc1.getPages()) {
				outDoc.addPage(pp.process(pg));
			}

			new PDFRenderer().renderAndSave(outDoc.build(), output);
			logger.info("Finished writing document");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderingException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}

	}

	private static class AlignmentSetter {
		private List<Object> align = new ArrayList<>(2);

		private void addAlignment(String className, double value) {
			Class<?> class1;
			try {
				class1 = Class.forName(Preprocessor.class.getName()+"$"+className);
				Constructor<?> constr1 = class1.getDeclaredConstructor(double.class);
				constr1.setAccessible(true);
				align.add(constr1.newInstance(0));
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
			         | InvocationTargetException | InstantiationException | SecurityException e) {
				e.printStackTrace();
			}
		}

		private void setAlignment(Preprocessor.Settings target) {
			Field alignment;
			try {
				alignment = Preprocessor.Settings.class.getDeclaredField("alignment");
				alignment.setAccessible(true);
				alignment.set(target, align);
			} catch (NoSuchFieldException | SecurityException
			         | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
