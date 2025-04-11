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

public class PFileWriter {
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

	public static void writeToFile(PResourceType resourceType, String name, String line) 
			throws PResourceException {
		writeToFile(resourceType, name, new String[]{line});
	}

	public static void writeToFile(PResourceType resourceType, String name, 
			String[] lines) throws PResourceException {
		PResource res = PResourceManager.fetchResource(resourceType, name);
		if (res == null) {
			throw new PResourceException(res, PResourceExceptionType.NOT_FOUND);

		}
		List<String> linesList = Arrays.asList(lines);
		try {
			File f = new File(res.getPath());
			System.out.println(f.exists());
			Files.write(res.getPathObject(),linesList, StandardCharsets.UTF_8,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new PResourceException(res, PResourceExceptionType.WRITE_FAIL);

		}
	}
}
