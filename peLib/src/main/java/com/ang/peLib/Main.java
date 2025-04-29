package com.ang.peLib;

import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.resources.*;
import com.ang.peLib.files.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("Debugging peLib");
		test();
	}

	private static void test() {
		PFileReader reader = new PFileReader();
		try {
			// PFileWriter.newFile(PResourceType.PMAP, PModuleName.EDITOR, "libTest.pmap");
			PFileWriter.writeToFile(PResourceType.PMAP, PModuleName.EDITOR, 
					"libTest.pmap", new String[]{"test line 1", "test line 2"});
			reader.readDirChildren(PResourceType.PMAP, PModuleName.EDITOR, false);
			PFileWriter.writeToFile(PResourceType.PMAP, PModuleName.EDITOR, 
					"libTest.pmap", new String[]{"new line 1", "new line 2"});
		} catch (PResourceException e) {
			e.printStackTrace();
		}
	}
}
