package com.ang.peLib.exceptions;

/**
 * Exception used when there is an error when parsing a .pmap file.
 */
public class PParseException extends Exception {
	/**
	 * Default constructor.
	 * @param message the message to display with this exception
	 */
	public PParseException(String message) {
		super(message);
	}

	/**
	 * Shorthand constructor for a generic error message.
	 * @param path    the path to the file where the error ocurred
	 * @param lineNum the number of the line where the error ocurred
	 */
	public PParseException(String path, int lineNum) {
		super("Syntax error at " + path + ":" + lineNum);
	}
}
