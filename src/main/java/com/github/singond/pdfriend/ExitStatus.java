package com.github.singond.pdfriend;

public enum ExitStatus {

	/**
	 * A simple operation (without data processing) completed successfully.
	 * This is the result of displaying usage help or program version.
	 */
	NOOP(true, 0),
	/**
	 * All operations requested by the user completed successfully.
	 */
	SUCCESS(true, 0),
	// According to https://stackoverflow.com/a/40484670/12904105,
	// bad invocations in *nix generally return code 2.
	/**
	 * Failure due to missing command.
	 * This signifies that no argument which can be treated as a command
	 * was given.
	 */
	MISSING_COMMAND(false, 2),
	/**
	 * Failure due to an unknown command.
	 * This signifies that an unknown command was given.
	 */
	UNKNOWN_COMMAND(false, 2),
	/**
	 * A generic error in program arguments.
	 */
	INVALID_ARGUMENT(false, 2),
	/**
	 * Error in processed data.
	 * This could mean, for example, a malformed PDF file.
	 */
	CORRUPT_DATA(false, 3),
	/**
	 * A generic error which does not fit into any of the other categories.
	 */
	OTHER_ERROR(false, 1);

	private final boolean success;
	private final int code;

	private ExitStatus(boolean success, int code) {
		this.success = success;
		this.code = code;
	}

	/**
	 * Returns {@code true} if the exit is considered a success.
	 *
	 * @return {@code true} if the exit is a success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Returns the exit code of this exit status.
	 *
	 * @return the exit code
	 */
	public int exitCode() {
		return code;
	}
}
