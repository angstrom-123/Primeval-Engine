package com.ang.peLib.files;

import com.ang.peLib.exceptions.*;
import com.ang.peLib.resources.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.io.File;

/**
 * Provides functions for reading files and directories.
 */
public class PResourceFileReader {
	/**
	 * Finds the files that are children of a given resource directory of a 
	 * given module.
	 * All paths are given relative to the resources directory. This will 
	 * fail if the directory structure of the engine is disrupted.
	 * @param  resourceType	 	  type of resource held in the directory to read
	 * @param  module 			  name of the module containing the dir to read
	 * @param  onlyChildren		  specifies if the paths to files should be 
	 * 							  included in the return, or only the filenames 
	 * @return 				      an array of paths that are subdirectories of 
	 * 							  the specified parent dir
	 * @throws PResourceException if there is a problem with reading directories
	 */
	public String[] readDirChildren(PResourceType resourceType, boolean onlyChildren) 
			throws PResourceException {
		final String SEP = System.getProperty("file.separator");
		Pattern pat = Pattern.compile(resourceType.getDirName() + SEP + ".+");
		String classpath = System.getProperty("java.class.path", ".");
		System.out.println("classpath: " + classpath);
		ArrayList<String> outList = new ArrayList<String>();
		ZipFile zf;
		try {
			zf = new ZipFile(new File(classpath));
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String entryString = entry.getName();
				if (pat.matcher(entryString).matches()) {
					outList.add(entryString);
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
		for (int i = 0; i < out.length; i++) {
			if (onlyChildren) {
				String[] path = outList.get(i).split(SEP);
				out[i] = path[path.length - 1];
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
	 * long as they are different resource types. 
	 * @param  resourceType		  the type of resource that is stored in the file
	 * @param  name				  the name of the file to read
	 * @param  module 			  the name of the module containing the file to read
	 * @return 					  array of strings where each element is a line 
	 * 							  from the specified file.
	 * @throws PResourceException if there is a problem with reading the file
	 * @see 					  #PFileReader(int)
	 * @see 				      com.ang.peLib.resources.PResourceType
	 * @see 				      com.ang.peLib.resources.PResourceManager
	 */
	public String[] readFile(PResourceType resourceType, String name) 
			throws PResourceException {
		PResource res = PResourceManager.fetch(resourceType, name);
		InputStream s = this.getClass().getResourceAsStream(res.getPath());
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		ArrayList<String> outList = new ArrayList<String>();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					outList.add(line);
				}
			}
			String[] out = new String[outList.size()];
			for (int i = 0; i < out.length; i++) {
				out[i] = outList.get(i);
			}
			return out;

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
	 * @param  module 			  the name of the module containing the file to read
	 * @param  name				  the name of the file to read
	 * @return 					  array of bytes where each element is a byte from 
	 * 							  the specified file
	 * @throws PResourceException if there is a problem with reading the file
	 * @see 				      com.ang.peLib.resources.PResourceType
	 * @see 				      com.ang.peLib.resources.PResourceManager
	 */
	public byte[] readFileAsBytes(PResourceType resourceType, String name) 
			throws PResourceException {
		PResource res = PResourceManager.fetch(resourceType, name);
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
