package com.ang.peLib.resources;

import com.ang.peLib.exceptions.*;

public class PResourceManager {
	// public final static String MAP_DIR_DEV = "
	public final static String MAP_DIR = "/map/";
	public final static String SPRITE_DIR = "/sprite/";

	public static PResource fetchResource(PResourceType resourceType, String name) 
			throws PResourceException {
		if (name.startsWith("/") || name.startsWith("\\")) {
			name = name.substring(1);
		}
		PResource res;
		switch (resourceType) {
		case PMAP:
			res = new PResource(resourceType, MAP_DIR + name);
			break;

		case SPRITE: // TODO : implement other resource types
			res = new PResource(resourceType, null); 
			break;
		
		default:
			res = PResource.invalid();
			break;

		}
		if (!res.exists()) {
			throw new PResourceException(res, PResourceExceptionType.NOT_FOUND);

		}
		if (!res.valid()) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		return res;

	}
}
