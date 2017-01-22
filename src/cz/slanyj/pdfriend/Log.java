package cz.slanyj.pdfriend;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
	public static final Logger logger = LogManager.getLogger(Log.class.getName());
	
	static {
		
	}
	
	private static String format(String msg, Object... objects) {
		return String.format(Locale.US, msg, objects);
	}
	
	public static void fatal(String msg) {
		logger.fatal(msg);
	}
	public static void fatal(String msg, Object... objects) {
		logger.fatal(format(msg, objects));
	}
	
	public static void error(String msg) {
		logger.error(msg);
	}
	public static void error(String msg, Throwable e) {
		logger.error(msg, e);
	}
	public static void error(String msg, Object... objects) {
		logger.error(format(msg, objects));
	}
	
	public static void warn(String msg) {
		logger.warn(msg);
	}
	public static void warn(String msg, Object... objects) {
		logger.warn(format(msg, objects));
	}
	
	public static void info(String msg) {
		logger.info(msg);
	}
	public static void info(String msg, Object... objects) {
		logger.info(format(msg, objects));
	}
	
	public static void debug(String msg) {
		logger.debug(msg);
	}
	public static void debug(String msg, Object... objects) {
		logger.debug(format(msg, objects));
	}
	
	public static void trace(String msg) {
		logger.trace(msg);
	}
	public static void trace(String msg, Object... objects) {
		logger.trace(format(msg, objects));
	}
	
	/** Test the log */
	public static void main(String[] args) {
		trace("Testing log4j configuration. This is trace level.");
		debug("Testing log4j configuration. This is debug level.");
		info ("Testing log4j configuration. This is info level.");
		warn ("Testing log4j configuration. This is warn level.");
		error("Testing log4j configuration. This is error level.");
		fatal("Testing log4j configuration. This is fatal level.");
	}
}
