package cz.slanyj.pdfriend;

import java.io.IOException;
import java.util.ResourceBundle;

public class Version {

	private final int major;
	private final int minor;
	private final int revision;
	private final String suffix;
	
	private Version() throws IOException {
		ResourceBundle versionInfo = ResourceBundle.getBundle("version");
		
		major = Integer.valueOf(versionInfo.getString("major"));
		minor = Integer.valueOf(versionInfo.getString("minor"));
		int rev = 0;
		try {
			rev = Integer.valueOf(versionInfo.getString("revision"));
		} catch (NumberFormatException e) {}
		revision = rev;
		suffix = versionInfo.getString("suffix");
	}
	
	private Version(int maj, int min, int rev, String suf) {
		major = maj;
		minor = min;
		revision = rev;
		suffix = suf;
	}
	
	/**
	 * Returns the version information.
	 */
	public static Version current() {
		try {
			return new Version();
		} catch (IOException e) {
			e.printStackTrace();
			return new Version(0, 0, 0, "");
		}
	}
	
	/**
	 * Returns the version number.
	 */
	public String toString() {
		if (suffix.isEmpty()) {
			return ""+major+"."+minor+"."+revision;
		}
		return ""+major+"."+minor+"."+revision+"_"+suffix;
	}
}
