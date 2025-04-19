package com.ang.peLib.files;

import com.ang.peLib.exceptions.*;
import com.ang.peLib.resources.*;
import com.ang.peLib.utils.PArrays;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.File;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Provides functions for reading files and directories.
 */
public class PFileReader {
	private int maxLines = 2000;

	/**
	 * Default constructor.
	 */
	public PFileReader() {}

	/**
	 * Constructor with custom maximum file length.
	 * @param maxLines maximum amount of lines allowed in a file
	 */
	public PFileReader(int maxLines) {
		this.maxLines = maxLines;
	}

	/**
	 * Finds the files that are children of a given directory.
	 * All returned paths are given relative to the resources directory under 
	 * "peLib/src/main/resources". This method reads the .jar (zip) file that is 
	 * created at compile time for its directory structure. This structure can be 
	 * considered immutable and therefore readonly. The specified directory is 
	 * matched using regex.
	 * @param  parentDir	 	  the name of the directory to find children of
	 * @param  onlyLeaves		  specifies if the path to the file should be 
	 * 							  included in the return, or only the filename 
	 * @return 				      an array of paths that are subdirectories of 
	 * 							  the specified parent dir
	 * @throws PResourceException if there is a problem with reading directories
	 */
	public String[] readDirChildren(String parentDir, boolean onlyLeaves) 
			throws PResourceException {
		String separator = System.getProperty("file.separator");
		if (parentDir.startsWith(separator)) {
			parentDir = parentDir.substring(1, parentDir.length());
		}
		if (parentDir.endsWith(separator)) {
			parentDir = parentDir.substring(0, parentDir.length() - 1);
		}
		Pattern pattern = Pattern.compile(parentDir + separator + ".+");
		String classPath = System.getProperty("java.class.path", ".");
		ArrayList<String> outList = new ArrayList<String>();
		ZipFile zf;
		try {
			zf = new ZipFile(new File(classPath));
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String filePath = entry.getName();
				if (pattern.matcher(filePath).matches()) {	
					outList.add(filePath);
				}
			}
			zf.close();
		} catch (ZipException e) {
			throw new PResourceException(PResource.invalid(), 
					PResourceExceptionType.READ_FAIL);

		} catch (IOException e) {
			throw new PResourceException(PResource.invalid(),
					PResourceExceptionType.READ_FAIL);

		}
		String[] out = new String[outList.size()];
		for (int i = 0; i < outList.size(); i++) {
			if (onlyLeaves) {
				// only output the deepest child (no path)
				String[] files = outList.get(i).split(separator);
				out[i] = files[files.length - 1];
			} else {
				out[i] = outList.get(i);
			}
		}
		return out;

	}

	/**
	 * Returns a resource file with a specified name as an array of strings.
	 * Resources are stored under multiple subdirectories. The specified resource 
	 * is searched for under the subdirectory listed by the resource manager for 
	 * the specified resource type. Multiple resources can have the same name as 
	 * long as they are different resource types. The maximum length of a file that 
	 * can be read is specified in the class constructor.
	 * @param  resourceType		  the type of resource that is stored in the file
	 * @param  name				  the name of the file to read
	 * @return 					  array of strings where each element is a line 
	 * 							  from the specified file.
	 * @throws PResourceException if there is a problem with reading the file
	 * @see 					  #PFileReader(int)
	 * @see 				      com.ang.peLib.resources.PResourceType
	 * @see 				      com.ang.peLib.resources.PResourceManager
	 */
	public String[] readFile(PResourceType resourceType, String name) 
			throws PResourceException {
		PResource res = PResourceManager.fetchResource(resourceType, name);
		if ((res == null) || (!res.exists()) || (!res.valid())) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		InputStream s = this.getClass().getResourceAsStream(res.getPath());
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		String[] lines = new String[maxLines];
		int head = 0;
		try {
			while (true) {
				if (head >= maxLines) {
					throw new PResourceException(res, PResourceExceptionType.TOO_LARGE);

				}
				String line = br.readLine();
				if (line == null) {
					return (String[]) PArrays.reduceArray(lines, head, String.class);

				}
				if (line.trim().length() > 0) {
					lines[head++] = line;
				}
			}
		} catch (IOException e) {
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
	}

	/**
	 * Returns a binary encoded resource file with a specified name as an array 
	 * of bytes.
	 * Resources are stored under multiple subdirectories. The specified resource 
	 * is searched for under the subdirectory listed by the resource manager for 
	 * the specified resource type. Multiple resources can have the same name as 
	 * long as they are different resource types.
	 * @param  resourceType		  the type of resource that is stored in the file
	 * @param  name				  the name of the file to read
	 * @return 					  array of bytes where each element is a byte from 
	 * 							  the specified file
	 * @throws PResourceException if there is a problem with reading the file
	 * @see 				      com.ang.peLib.resources.PResourceType
	 * @see 				      com.ang.peLib.resources.PResourceManager
	 */
	public byte[] readFileAsBytes(PResourceType resourceType, String name) 
			throws PResourceException {
		PResource res = PResourceManager.fetchResource(resourceType, name);
		if ((res == null) || (!res.exists()) || (!res.valid())) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		InputStream s = this.getClass().getResourceAsStream(res.getPath());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		try {
			while (true) {
				int read = s.read(buffer);
				if (read <= 0) {
					return baos.toByteArray();

				}
				baos.write(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
	}
}
