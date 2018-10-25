package com.github.singond.pdfriend.io;

class FileNameHandler {

	private FileNameHandler() {
		throw new UnsupportedOperationException("Non-instantiable class");
	}

	public static String normalizeFileName(String fileName) {
		return fileName.replace(" ", "_");
	}
}
