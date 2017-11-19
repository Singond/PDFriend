package com.github.singond.pdfriend.cli;

public enum ExitStatus {

	/** All operations requested by the user completed successfully */
	SUCCESS,
	/** Error in input */
	INPUT_FAILURE,
	/** Error in processed data */
	DATA_FAILURE,
	/** Unknown error occured */
	FAILURE;
}
