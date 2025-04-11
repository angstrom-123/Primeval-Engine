package com.ang.peLib.utils;

import com.ang.peLib.maths.PVec2;

public class PConversionUtils {
	public static int[] v2ss(PVec2 p0, PVec2 p1, 
			int screenHeight, int screenWidth, double scale, PVec2 viewPos) {
		return new int[]{
			v2ss(p0.x(), true, screenWidth, screenHeight, 
					scale, viewPos),
			v2ss(p0.y(), false, screenWidth, screenHeight, 
					scale, viewPos),
			v2ss(p1.x(), true, screenWidth, screenHeight, 
					scale, viewPos),
			v2ss(p1.y(), false, screenWidth, screenHeight, 
					scale, viewPos)};

	}

	public static int[] v2ss(PVec2 p, 
			int screenHeight, int screenWidth, double scale, PVec2 viewPos) {
		return new int[]{
			v2ss(p.x(), true, screenWidth, screenHeight,
					scale, viewPos),
			v2ss(p.y(), false, screenWidth, screenHeight,
					scale, viewPos)};

	}

	public static int v2ss(double coord, boolean isX,
			int screenHeight, int screenWidth, double scale, PVec2 viewPos) {
		int scaled = (int) Math.round(coord * scale);
		int transpose = isX 
		? (screenWidth / 2) + (int) viewPos.x() 
		: (screenHeight / 2) + (int) viewPos.y();
		return scaled + transpose;

	}

	public static double[] ss2v(int x, int y,
			int screenHeight, int screenWidth, double scale, PVec2 viewPos) {
		return new double[]{
			ss2v(x, true, screenHeight, screenWidth, 
					scale, viewPos),
			ss2v(y, false, screenHeight, screenWidth, 
					scale, viewPos)};
	}

	public static double ss2v(int coord, boolean isX,
			int screenHeight, int screenWidth, double scale, PVec2 viewPos) {
		double transpose = isX
		? ((double) screenWidth / 2) + viewPos.x()
		: ((double) screenHeight / 2) + viewPos.y();
		return ((double) (coord - transpose)) / scale;

	}
}
