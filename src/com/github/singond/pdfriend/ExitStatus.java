package com.github.singond.pdfriend;

public enum ExitStatus {

	/** A simple operation (no data processing) completed successfully */
	SIMPLE,
	/** All operations requested by the user completed successfully */
	SUCCESS,
	/** Unknown command */
	UNKNOWN_COMMAND,
	/** Error in input */
	INPUT_FAILURE,
	/** Error in processed data */
	DATA_FAILURE,
	/** Unknown error occured */
	FAILURE;
}
