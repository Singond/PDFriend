package com.github.singond.pdfriend;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public abstract class Util {

	/**
	 * Returns the current working directory.
	 * @return A new object representing the working directory.
	 */
	public static File getWorkingDir() {
		return new File(System.getProperty("user.dir"));
	}
	
	/**
	 * Returns the parent directory of the running application.
	 * The parent is the parent directory of the jar file, if running
	 * from jar. When running from Eclipse IDE, the parent is ../bin.
	 * @return null if the directory cannot be determined.
	 */
	public static File getApplicationDir() {
		try {
			CodeSource src = Main.class.getProtectionDomain().getCodeSource();
			File srcPath = new File(src.getLocation().toURI().getPath());
			return srcPath.getParentFile();
		} catch (SecurityException e) {
			// Thrown by getProtectionDomain
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the quotient of the two numbers rounded to integer upwards.
	 * @param dividend
	 * @param divisor
	 * @return
	 */
	public static int ceilingDivision(int dividend, int divisor) {
		return (int) (Math.ceil(((float)dividend)/divisor));
	}
	
	/**
	 * Provides alternative string representation for AffineTransform.
	 * @param matrix the matrix to be converted to string
	 * @return a string representation of the matrix 
	 */
	public static String toString(AffineTransform matrix) {
		return String.format("[[%7.3f, %7.3f, %7.3f], [%7.3f, %7.3f, %7.3f]]",
		                     matrix.getScaleX(),
		                     matrix.getShearX(),
		                     matrix.getTranslateX(),
		                     matrix.getShearY(),
		                     matrix.getScaleY(),
		                     matrix.getTranslateY());
	}
}
