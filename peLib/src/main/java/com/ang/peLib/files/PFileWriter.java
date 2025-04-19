package com.ang.peLib.files;

import com.ang.peLib.exceptions.*;
import com.ang.peLib.resources.*;

import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;

/**
 * Provides methods for writing lines to resource files.
 */
public class PFileWriter {
	/**
	 * Creates a new resource file with a specified name.
	 * The path to the new file is specified by the resource type. The resource 
	 * manager specifies a path from the resources directory to the directory where 
	 * each type is stored.
	 * @param  resourceType 	  the type of resource to be stored in the file
	 * @param  name 			  name of the file to create
	 * @throws PResourceException if there is a problem with creating the file
	 * @see    					  com.ang.peLib.resources.PResourceManager
	 * @see    					  com.ang.peLib.resources.PResourceType
	 */
	public static void newFile(PResourceType resourceType, String name) 
			throws PResourceException {
		PResource temp = PResourceManager.fetchResource(resourceType, name);
		if (temp != null) {
			throw new PResourceException(temp, PResourceExceptionType.ALREADY_EXISTS);

		}
		PResource res = new PResource(resourceType, name);
		try {
			File f = new File(res.getPath());
			if (!f.createNewFile()) {
				throw new PResourceException(res, PResourceExceptionType.CREATE_FAIL);

			}
		} catch (IOException e) {
			throw new PResourceException(res, PResourceExceptionType.CREATE_FAIL);

		}
	}

	/**
	 * Writes to an existing resource file with a specified name.
	 * Overrides writeToFile to write a single string to the file.
	 * <p>
	 * The path to the file is specified by the resource type. The resource 
	 * manager specifies a path from the resources directory to the directory where 
	 * each type is stored.
	 * @param  resourceType 	  the type of resource stored in the file
	 * @param  name 			  name of the file to write to 
	 * @param  line 			  the line to write to the file
	 * @throws PResourceException if there is a problem with creating the file
	 * @see 					  #writeToFile(PResourceType, String, String[])
	 * @see    					  com.ang.peLib.resources.PResourceManager
	 * @see    					  com.ang.peLib.resources.PResourceType
	 */
	public static void writeToFile(PResourceType resourceType, String name, String line) 
			throws PResourceException {
		writeToFile(resourceType, name, new String[]{line});
	}

	/**
	 * Writes to an existing resource file with a specified name.
	 * The path to the file is specified by the resource type. The resource 
	 * manager specifies a path from the resources directory to the directory where 
	 * each type is stored.
	 * @param  resourceType 	  the type of resource stored in the file
	 * @param  name 			  name of the file to write to 
	 * @param  lines 			  the lines to write to the file
	 * @throws PResourceException if there is a problem with creating the file
	 * @see 					  #writeToFile(PResourceType, String, String)
	 * @see    					  com.ang.peLib.resources.PResourceManager
	 * @see    					  com.ang.peLib.resources.PResourceType
	 */
	public static void writeToFile(PResourceType resourceType, String name, 
			String[] lines) throws PResourceException {
		PResource res = PResourceManager.fetchResource(resourceType, name);
		if (res == null) {
			throw new PResourceException(res, PResourceExceptionType.NOT_FOUND);

		}
		// Files.write() accepts a list of strings, not an array
		List<String> linesList = Arrays.asList(lines);
		try {
			File f = new File(res.getPath());
			System.out.println(f.exists());
			Files.write(res.getPathObject(), linesList, StandardCharsets.UTF_8,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new PResourceException(res, PResourceExceptionType.WRITE_FAIL);

		}
	}
}
