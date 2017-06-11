package cz.slanyj.pdfriend;

import java.io.File;
import org.apache.logging.log4j.message.LocalizedMessageFactory;

public class Log {
	
	static {
		File appDir = Util.getApplicationDir();
		if (appDir != null) {
			System.setProperty("logFile", appDir.getAbsolutePath());
		} else {
			System.setProperty("logFile", "logs");
		}
	}
	
	public static ExtendedLogger logger(Object object) {
		return ExtendedLogger.create(object.getClass(),
		                             new LocalizedMessageFactory(Bundle.console));
	}
	
	
	/** Test the log */
	public static void main(String[] args) {
		final ExtendedLogger l = logger(Log.class);
		l.trace("Testing log4j configuration. This is trace level.");
		l.debug("Testing log4j configuration. This is debug level.");
		l.verbose("Testing log4j configuration. This is verbose level.");
		l.info ("Testing log4j configuration. This is info level.");
		l.warn ("Testing log4j configuration. This is warn level.");
		l.error("Testing log4j configuration. This is error level.");
		l.fatal("Testing log4j configuration. This is fatal level.");
	}
}
