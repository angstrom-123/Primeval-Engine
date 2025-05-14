package com.ang.peLib;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.resources.*;
import com.ang.peLib.files.*;
import com.ang.peLib.files.json.PJSONParser;

public class Main {
	public static void main(String[] args) {
		System.out.println("Debugging peLib");
		test();
	}

	private static void test() {
		PFileReader reader = new PFileReader();
		try {
			String[] lines = reader.readFile(PResourceType.CONFIG, PModuleName.EDITOR, 
											 "config.json");
			PJSONParser parser = new PJSONParser("conf");
			parser.parseJSONData(lines);
		} catch (PResourceException e) {
			e.printStackTrace();
		} catch (PParseException e) {
			e.printStackTrace();
		}
	}
}
