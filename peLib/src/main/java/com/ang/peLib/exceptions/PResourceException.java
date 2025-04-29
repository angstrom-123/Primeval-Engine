package com.ang.peLib.exceptions;

import com.ang.peLib.resources.PResource;

/**
 * Exception used when there is an error saving / loading a resource.
 * @see com.ang.peLib.resources.PResource
 */
public class PResourceException extends Exception {
	/**
	 * Default constructor.
	 * @param message the message to display with this exception
	 */
	public PResourceException(String message) {
		super(message);	
	}

	/**
	 * Shorthand constructor for a generic error message.
	 * @param res  the resource that caused the exception
	 * @param type the type of the exception to throw (differing error messages)
	 * @see 	   PResourceExceptionType
	 */
	public PResourceException(PResource res, PResourceExceptionType type) {
		super("Resource exception at " + pathOutput(res) + " : " + type.getMessage());
	}

	/**
	 * Gets the path from the resource that caused an exception.
	 * @return the path to the resource or {@code null} if the path cannot be found
	 * @see    com.ang.peLib.resources.PResource
	 */
	private static String pathOutput(PResource res) {
		if (res == null) {
			return "unknown";

		}
		String path = res.getPath();
		return (path == null) ? "unknown" : path;

	}
}
