package com.ang.peLib.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ang.peLib.exceptions.*;

/**
 * Manages resources.
 */
public class PResourceManager {
	private final static String SEP = System.getProperty("file.separator");
	public final static String SPRITE_DIR = SEP + PResourceType.SPRITE.getDirName() + SEP;
	public final static String MAP_DIR = SEP + PResourceType.PMAP.getDirName() + SEP;
	public final static String CONFIG_DIR = SEP + PResourceType.CONFIG.getDirName() + SEP;
	private final static String RES_PATH = "src" + SEP + "main" + SEP + "resources" + SEP;
	private static String editorMapDir = null;
	private static String editorSpriteDir = null;
	private static String coreMapDir = null;
	private static String coreSpriteDir = null;

	/**
	 * Returns the directory to the resource map directory of the editor module.
	 * @return 					  the directory to the editor map resources 
	 * @throws PResourceException if the directory cannot be found (for example 
	 * 							  if the .jar file is not in the default location)
	 */
	public static String getEditorMapDir() throws PResourceException {
		if (editorMapDir == null) {
			editorMapDir = getDirOf(PResourceType.PMAP, PModuleName.EDITOR);
		}
		return editorMapDir;

	}

	/**
	 * Returns the directory to the resource sprite directory of the editor module.
	 * @return 					  the directory to the editor sprite resources 
	 * @throws PResourceException if the directory cannot be found (for example 
	 * 							  if the .jar file is not in the default location)
	 */
	public static String getEditorSpriteDir() throws PResourceException {
		if (editorSpriteDir == null) {
			editorSpriteDir = getDirOf(PResourceType.SPRITE, PModuleName.EDITOR);
		}
		return editorSpriteDir;

	}

	/**
	 * Returns the directory to the resource map directory of the core module.
	 * @return 					  the directory to the core map resources 
	 * @throws PResourceException if the directory cannot be found (for example 
	 * 							  if the .jar file is not in the default location)
	 */
	public static String getCoreMapDir() throws PResourceException {
		if (coreMapDir == null) {
			coreMapDir = getDirOf(PResourceType.PMAP, PModuleName.CORE);
		}
		return coreMapDir;

	}

	/**
	 * Returns the directory to the resource sprite directory of the core module.
	 * @return 					  the directory to the core sprite resources 
	 * @throws PResourceException if the directory cannot be found (for example 
	 * 							  if the .jar file is not in the default location)
	 */
	public static String getCoreSpriteDir() throws PResourceException {
		if (coreSpriteDir == null) {
			coreSpriteDir = getDirOf(PResourceType.SPRITE, PModuleName.CORE);
		}
		return coreSpriteDir;

	}

	/**
	 * Returns the directory to a resource type in a different module.
	 * @param  resourceType 	  the type of resource to get the directory of
	 * @param  module			  the name of the module to get the directory from
	 * @return 					  directory to the specified module's resource
	 * @throws PResourceException if the directory cannot be found (for example 
	 * 							  if the .jar file is not in the default location)
	 * @see	   					  PResourceType
	 * @see	   					  PModuleName
	 */
	public static String getDirOf(PResourceType resourceType, PModuleName module) 
			throws PResourceException {
		String jarPathUndecoded = PResourceManager.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		String jarPath;
		try {
			jarPath = URLDecoder.decode(jarPathUndecoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new PResourceException(null, PResourceExceptionType.NOT_FOUND);

		}
		String resourcePath = "";
		for (String dirName : jarPath.split(SEP)) {
			resourcePath += dirName + SEP;
			if (dirName.equals(PModuleName.PARENT.getDirName())) {
				resourcePath += module.getDirName() + SEP + RES_PATH 
						+ resourceType.getDirName() + SEP;
				return resourcePath;

			}
		}
		throw new PResourceException(null, PResourceExceptionType.NOT_FOUND);

	}

	/**
	 * Attempts to fetch a specified resource from the local jar resources.
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
	public static PResource fetch(PResourceType resourceType, String name) 
			throws PResourceException {
		if (name.startsWith(System.getProperty("file.separator"))) {
			name = name.substring(1);
		}
		PResource res;
		switch (resourceType) {
		case PMAP:
			res = new PResource(resourceType, PResourceManager.MAP_DIR + name, true);
			break;

		case SPRITE:
			res = new PResource(resourceType, PResourceManager.SPRITE_DIR + name, true); 
			break;

		case CONFIG:
			res = new PResource(resourceType, PResourceManager.CONFIG_DIR + name, true);
			break;
		
		default:
			throw new PResourceException(PResource.invalid(), 
					PResourceExceptionType.INVALID);

		}
		if (!res.exists()) {
			throw new PResourceException(res, PResourceExceptionType.NOT_FOUND);

		}
		if (!res.valid()) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		return res;

	}

	/**
	 * Attempts to fetch a specified resource from a different module.
	 * Resources are held in different directories according to their resource 
	 * type. These directories are held in this class as constants. The particular 
	 * subdirectory of resources to fetch from is specified by the resource type.
	 * @param  resourceType		  resource type of the resource to fetch
	 * @param  module 			  the name of the module containing the resource
	 * @param  name 		      the filename of the resource to fetch
	 * @return 					  the specified resource
	 * @throws PResourceException if there is a problem with finding the resource
	 * @see 					  PResource
	 * @see 					  PResourceType
	 */
	public static PResource fetch(PResourceType resourceType, PModuleName module, 
			String name) throws PResourceException {
		if (name.startsWith(System.getProperty("file.separator"))) {
			name = name.substring(1);
		}
		PResource res;
		switch (resourceType) {
		case PMAP:
			res = new PResource(resourceType, getDirOf(resourceType, module) + name, false);
			break;

		case SPRITE:
			res = new PResource(resourceType, getDirOf(resourceType, module) + name, false); 
			break;

		case CONFIG:
			res = new PResource(resourceType, getDirOf(resourceType, module) + name, false);
			break;
		
		default:
			throw new PResourceException(PResource.invalid(), 
					PResourceExceptionType.INVALID);

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
