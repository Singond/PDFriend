package com.github.singond.pdfriend;

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
}
