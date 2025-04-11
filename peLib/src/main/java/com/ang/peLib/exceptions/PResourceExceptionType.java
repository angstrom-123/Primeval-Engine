package com.ang.peLib.exceptions;

public enum PResourceExceptionType {
	NOT_FOUND("Resource not found"),
	INVALID("File is not a valid resource"),
	ALREADY_EXISTS("Resource already exists"),
	WRITE_FAIL("Failed to write to resource"),
	READ_FAIL("Failed to read from resource"),
	CREATE_FAIL("Failed to create resource"),
	TOO_LARGE("Resource is too large");

	private String message;

	private PResourceExceptionType(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;

	}
}
