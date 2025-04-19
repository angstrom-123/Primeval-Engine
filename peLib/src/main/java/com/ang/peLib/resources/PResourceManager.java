package com.ang.peLib.resources;

import com.ang.peLib.exceptions.*;

/**
 * Manages resources.
 */
public class PResourceManager {
	public final static String MAP_DIR = "/map/";
	public final static String SPRITE_DIR = "/sprite/";

	/**
	 * Attempts to fetch a specified resource.
	 * Resources are held in different directories according to their resource 
	 * type. These directories are held in this class as constants. The particular 
	 * subdirectory of resources to fetch from is specified by the resource type.
	 * @param  resourceType		  resource type of the resource to fetch
	 * @param  name 		      the filename of the resource to fetch
	 * @return 					  the specified resource
	 * @throws PResourceException if there is a problem with finding the resource
	 * @see 					  PResource
	 * @see 					  PResourceType
	 */
	public static PResource fetchResource(PResourceType resourceType, String name) 
			throws PResourceException {
		if (name.startsWith(System.getProperty("file.separator"))) {
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
