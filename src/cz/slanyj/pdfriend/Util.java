package cz.slanyj.pdfriend;

import java.io.File;

public abstract class Util {

	/**
	 * Returns the current working directory.
	 * @return A new object representing the working directory.
	 */
	public static File getWorkingDir() {
		return new File(System.getProperty("user.dir"));
	}
	
}
