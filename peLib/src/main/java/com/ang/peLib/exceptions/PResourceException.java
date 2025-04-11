package com.ang.peLib.exceptions;

import com.ang.peLib.resources.PResource;

public class PResourceException extends Exception {
	public PResourceException(String message) {
		super(message);	
	}

	public PResourceException(PResource res, PResourceExceptionType type) {
		super("Resource exception at " + pathOutput(res) + " : " + type.getMessage());
	}

	private static String pathOutput(PResource res) {
		String path = res.getPath();
		return (path == null) ? "unknown" : path;

	}
}
