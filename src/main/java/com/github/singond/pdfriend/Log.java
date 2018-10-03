package com.github.singond.pdfriend;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.message.LocalizedMessageFactory;

public class Log {
	public static final Level VERBOSE = Level.forName("VERBOSE", 450);
	
	static {
		File appDir = Util.getApplicationDir();
		if (appDir != null) {
			System.setProperty("logFile", appDir.getAbsolutePath());
		} else {
			System.setProperty("logFile", "logs");
		}
	}
	
	/**
	 * Returns an ExtendedLogger instance with the given name
	 * and using the Console resource bundle for its messages.
	 * @param name the name of the logger
	 * @return an instace of ExtendedLogger
	 */
	public static ExtendedLogger logger(String name) {
		return ExtendedLogger.create
				(name, new LocalizedMessageFactory(Bundle.console));
	}
	
	/**
	 * Returns an ExtendedLogger instance with the name of the given class
	 * and using the Console resource bundle for its messages.
	 * @param cls the class the logger is to be named after
	 * @return an instace of ExtendedLogger
	 */
	public static ExtendedLogger logger(Class<?> cls) {
		return ExtendedLogger.create
				(cls, new LocalizedMessageFactory(Bundle.console));
	}
	
	/**
	 * Sets the logging level for the root logger.
	 * @param level the level to be set
	 */
	public static void setLevel(Level level) {
		Configurator.setLevel(LogManager.ROOT_LOGGER_NAME, level);
	}
	
	
	/** Test the log */
	public static void main(String[] args) {
		final ExtendedLogger l = logger(Log.class);
		setLevel(Level.INFO);
		l.trace  ("Testing log4j configuration. This is trace level.");
		l.debug  ("Testing log4j configuration. This is debug level.");
		l.verbose("Testing log4j configuration. This is verbose level.");
		l.info   ("Testing log4j configuration. This is info level.");
		l.warn   ("Testing log4j configuration. This is warn level.");
		l.error  ("Testing log4j configuration. This is error level.");
		l.fatal  ("Testing log4j configuration. This is fatal level.");
	}
}
