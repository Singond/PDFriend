package cz.slanyj.pdfriend;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
	
	static {
		locale = Locale.US;
		File appDir = Util.getApplicationDir();
		if (appDir != null) {
			System.setProperty("logFile", appDir.getAbsolutePath());
		} else {
			System.setProperty("logFile", "logs");
		}
	}
	
	public static final Logger logger = LogManager.getLogger(Log.class.getName());
	
	public static final Level VERBOSE = Level.forName("VERBOSE", 450);
	
	private static Locale locale;
	
	/** Formats a string using the locale currently set in this class. */
	private static String format(String msg, Object... objects) {
		return String.format(locale, msg, objects);
	}
	
	/** Gets a string from the given resource bundle. */
	private static String getFrom(ResourceBundle bundle, String key) {
		return bundle.getString(key);
	}
	
	/**
	 * Gets a string from a given resource bundle and formats it using
	 * the current locale in this class.
	 */
	private static String formatFrom(ResourceBundle bundle, String key, Object... objects) {
		return format(getFrom(bundle, key), objects);
	}
	
	public static void fatal(String msg) {
		logger.fatal(msg);
	}
	public static void fatal(ResourceBundle bnd, String key) {
		logger.fatal(getFrom(bnd, key));
	}
	public static void fatal(String msg, Object... objects) {
		logger.fatal(format(msg, objects));
	}
	public static void fatal(ResourceBundle bnd, String key, Object... objects) {
		logger.fatal(formatFrom(bnd, key, objects));
	}
	
	public static void error(String msg) {
		logger.error(msg);
	}
	public static void error(ResourceBundle bnd, String key) {
		logger.error(getFrom(bnd, key));
	}
	public static void error(String msg, Throwable e) {
		logger.error(msg, e);
	}
	public static void error(String msg, Object... objects) {
		logger.error(format(msg, objects));
	}
	public static void error(ResourceBundle bnd, String key, Object... objects) {
		logger.error(formatFrom(bnd, key, objects));
	}
	
	public static void warn(String msg) {
		logger.warn(msg);
	}
	public static void warn(ResourceBundle bnd, String key) {
		logger.warn(getFrom(bnd, key));
	}
	public static void warn(String msg, Object... objects) {
		logger.warn(format(msg, objects));
	}
	public static void warn(ResourceBundle bnd, String key, Object... objects) {
		logger.warn(formatFrom(bnd, key, objects));
	}
	
	public static void info(String msg) {
		logger.info(msg);
	}
	public static void info(ResourceBundle bnd, String key) {
		logger.info(getFrom(bnd, key));
	}
	public static void info(String msg, Object... objects) {
		logger.info(format(msg, objects));
	}
	public static void info(ResourceBundle bnd, String key, Object... objects) {
		logger.info(formatFrom(bnd, key, objects));
	}
	
	public static void verbose(String msg) {
		logger.log(VERBOSE, msg);
	}
	public static void verbose(ResourceBundle bnd, String key) {
		logger.log(VERBOSE, getFrom(bnd, key));
	}
	public static void verbose(String msg, Object... objects) {
		logger.log(VERBOSE, format(msg, objects));
	}
	public static void verbose(ResourceBundle bnd, String key, Object... objects) {
		logger.log(VERBOSE, formatFrom(bnd, key, objects));
	}
	
	public static void debug(String msg) {
		logger.debug(msg);
	}
	public static void debug(ResourceBundle bnd, String key) {
		logger.debug(getFrom(bnd, key));
	}
	public static void debug(String msg, Object... objects) {
		logger.debug(format(msg, objects));
	}
	public static void debug(ResourceBundle bnd, String key, Object... objects) {
		logger.debug(formatFrom(bnd, key, objects));
	}
	
	public static void trace(String msg) {
		logger.trace(msg);
	}
	public static void trace(ResourceBundle bnd, String key) {
		logger.trace(getFrom(bnd, key));
	}
	public static void trace(String msg, Object... objects) {
		logger.trace(format(msg, objects));
	}
	public static void trace(ResourceBundle bnd, String key, Object... objects) {
		logger.trace(formatFrom(bnd, key, objects));
	}
	
	/** Test the log */
	public static void main(String[] args) {
		trace("Testing log4j configuration. This is trace level.");
		debug("Testing log4j configuration. This is debug level.");
		verbose("Testing log4j configuration. This is verbose level.");
		info ("Testing log4j configuration. This is info level.");
		warn ("Testing log4j configuration. This is warn level.");
		error("Testing log4j configuration. This is error level.");
		fatal("Testing log4j configuration. This is fatal level.");
	}
}
