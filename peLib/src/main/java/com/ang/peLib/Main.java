package com.ang.peLib;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.resources.*;
import com.ang.peLib.utils.PConversions;
import com.ang.peLib.files.*;
import com.ang.peLib.files.json.PJSONParser;
import com.ang.peLib.maths.PVec2;

public class Main {
	public static void main(String[] args) {
		System.out.println("Debugging peLib");
		test();
	}

	private static void test() {
		int width = 1200;
		int height = (int) Math.round(1200 / (16 / 9.0));
		double scale = 15.0;
		PVec2 viewPos = new PVec2(0.0, 0.0);
		int xSs = 10;
		int ySs = 15;
		System.out.println("before: " + xSs + " " + ySs);
		double[] coordsV = PConversions.ss2v(xSs, ySs, width, height, scale, viewPos);
		System.out.println("to viewport: " + coordsV[0] + " " + coordsV[1]);
		int[] coordsSs = PConversions.v2ss(new PVec2(coordsV), width, height, scale, viewPos);
		System.out.println("back to screenspace: " + coordsSs[0] + " " + coordsSs[1]);
		System.out.println("=======================================");
		PVec2 xyV = new PVec2(1.0, 2.0);
		System.out.println("before: " + xyV.x() + " " + xyV.y());
		int[] newCoordsSs = PConversions.v2ss(xyV, width, height, scale, viewPos);
		System.out.println("to screenspace: " + newCoordsSs[0] + " " + newCoordsSs[1]);
		double[] newCoordsV = PConversions.ss2v(newCoordsSs[0], newCoordsSs[1], width, height, scale, viewPos);
		System.out.println("back to viewport: " + newCoordsV[0] + " " + newCoordsV[1]);

		// PFileReader reader = new PFileReader();
		// try {
		// 	String[] lines = reader.readFile(PResourceType.CONFIG, PModuleName.EDITOR, 
		// 									 "config.json");
		// 	PJSONParser parser = new PJSONParser("conf");
		// 	parser.parseJSONData(lines);
		// } catch (PResourceException e) {
		// 	e.printStackTrace();
		// } catch (PParseException e) {
		// 	e.printStackTrace();
		// }
	}
}
