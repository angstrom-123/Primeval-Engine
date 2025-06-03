package com.ang.peLib;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.resources.*;
import com.ang.peLib.utils.PConversions;
import com.ang.peLib.utils.PConvexDecomposer;
import com.ang.peLib.files.*;
import com.ang.peLib.files.json.PJSONParser;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.files.pmap.PPMapParser;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.maths.PVec2;

public class Main {
	public static void main(String[] args) {
		System.out.println("Debugging peLib");
		test();
	}

	private static void test() {
		PFileReader reader = new PFileReader();
		try {
			String[] lines = reader.readFile(PResourceType.PMAP, PModuleName.CORE, 
											 "test.pmap");
			PPMapParser parser = new PPMapParser("entrypoint");
			PPMapData mapData = parser.parseMapData(lines);
			PSectorWorld world = mapData.world;
			PConvexDecomposer decomposer = new PConvexDecomposer(world);
			world = decomposer.decompose();
		} catch (PResourceException e) {
			e.printStackTrace();
		} catch (PParseException e) {
			e.printStackTrace();
		}
	}
}
