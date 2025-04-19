package com.ang.peLib.exceptions;

/**
 * Holds different types of resource exception and their corresponding messages.
 * @see PResourceException
 * @see com.ang.peLib.resources.PResource
 */
public enum PResourceExceptionType {
	NOT_FOUND("Resource not found"),
	INVALID("File is not a valid resource"),
	ALREADY_EXISTS("Resource already exists"),
	WRITE_FAIL("Failed to write to resource"),
	READ_FAIL("Failed to read from resource"),
	CREATE_FAIL("Failed to create resource"),
	TOO_LARGE("Resource is too large");

	private String message;

	/**
	 * Private constructor for setting the error message.
	 * @param message error message for this exception type
	 */
	private PResourceExceptionType(String message) {
		this.message = message;
	}

	/**
	 * Returns the message for this exception.
	 * @return error message for this exception
	 */
	public String getMessage() {
		return message;

	}
}
