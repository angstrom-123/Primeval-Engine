package com.ang.peLib.exceptions;

public class PParseException extends Exception {
	public PParseException(String message) {
		super(message);
	}

	public PParseException(String path, int lineNum) {
		super("Syntax error at " + path + ":" + lineNum);
	}
}
