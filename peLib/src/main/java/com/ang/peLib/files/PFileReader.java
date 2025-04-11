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

public class PFileReader {
	private int maxLines = 2000;

	public PFileReader() {}

	public PFileReader(int maxLines) {
		this.maxLines = maxLines;
	}

	public String[] readSubdirectories(String parentDir, boolean onlyChildren) 
			throws PResourceException {
		// cleaning pattern for regex
		if (parentDir.startsWith(System.getProperty("file.separator"))) {
			parentDir = parentDir.substring(1, parentDir.length());
		}
		if (parentDir.endsWith(System.getProperty("file.separator"))) {
			parentDir = parentDir.substring(0, parentDir.length() - 1);
		}
		/*
		 * regex matches for parent dir followed by a file separator and at least
		 * one characer (to avoid match of parent directory with no children)
		 */
		Pattern pattern = Pattern.compile(parentDir + File.separator + ".+");
		String classPath = System.getProperty("java.class.path", ".");
		/*
		 * ZipFile used to read contents of .jar classPath where resources
		 * are stored.
		 */
		ArrayList<String> outList = new ArrayList<String>();
		ZipFile zf = null;
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
			/*
			 * Stripping directories from result if onlyChildren specified
			 */
			if (onlyChildren) {
				String[] files = outList.get(i).split(
						System.getProperty("file.separator"));
				out[i] = files[files.length - 1];
			} else {
				out[i] = outList.get(i);
			}
		}
		return out;

	}

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
