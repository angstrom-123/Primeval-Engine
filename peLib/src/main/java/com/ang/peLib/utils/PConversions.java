package com.ang.peLib.utils;

import com.ang.peLib.maths.PVec2;

/**
 * Provides utility functions for converting between screenspace and viewport 
 * coordinates.
 */
public class PConversions {
	/**
	 * "Viewport to Screen Space" Converts two pairs of viewport coordinates 
	 * to screenspace.
	 * This method always returns the unclamped coordinates, the output can be 
	 * outside of the bounds of the screen.
	 * @param  p0 			first viewport coordinate to convert
	 * @param  p1 			second viewport coordinate to convert
	 * @param  screenHeight height (in pixels) of the screenspace window
	 * @param  screenWidth  width (in pixels) of the screenspace window
	 * @param  scale 		screenspace zoom multiplier
	 * @param  viewPos 		coordinates of the origin of the viewport
	 * @return			    an array of 4 integers representing the screenspace 
	 * 						coordinates of the specified vectors in the order 
	 * 						{p0.x, p0.y, p1.x, p1.y} (measured in pixels)
	 */
	public static int[] v2ss(PVec2 p0, PVec2 p1, int screenHeight, int screenWidth, 
			double scale, PVec2 viewPos) {
		return new int[]{
			v2ss(p0.x(), true, screenWidth, screenHeight, scale, viewPos),
			v2ss(p0.y(), false, screenWidth, screenHeight, scale, viewPos),
			v2ss(p1.x(), true, screenWidth, screenHeight, scale, viewPos),
			v2ss(p1.y(), false, screenWidth, screenHeight, scale, viewPos)};

	}

	/**
	 * "Viewport to Screen Space" Converts a pair viewport coordinates to 
	 * screenspace.
	 * This method always returns the unclamped coordinates, the output can be 
	 * outside of the bounds of the screen.
	 * @param  p 			viewport coordinate to convert
	 * @param  screenHeight height (in pixels) of the screenspace window
	 * @param  screenWidth  width (in pixels) of the screenspace window
	 * @param  scale 		screenspace zoom multiplier
	 * @param  viewPos 		coordinates of the origin of the viewport
	 * @return				an array of 2 integers representing the screenspace 
	 * 						coordinates of the specified vectors in the order 
	 * 						{p0.x, p0.y} (measured in pixels)
	 */
	public static int[] v2ss(PVec2 p, int screenHeight, int screenWidth, 
			double scale, PVec2 viewPos) {
		return new int[]{
			v2ss(p.x(), true, screenWidth, screenHeight, scale, viewPos),
			v2ss(p.y(), false, screenWidth, screenHeight, scale, viewPos)};

	}

	/**
	 * "Viewport to Screen Space" Converts a single viewport coordinate to 
	 * screenspace.
	 * This method always returns the unclamped coordinate, the output can be 
	 * outside of the bounds of the screen.
	 * @param  coord 		viewport coordinate to convert
	 * @param  isX   		specifies if the input coordinate represents x or y
	 * @param  screenHeight height (in pixels) of the screenspace window
	 * @param  screenWidth  width (in pixels) of the screenspace window
	 * @param  scale 		screenspace zoom multiplier
	 * @param  viewPos 		coordinates of the origin of the viewport
	 * @return       		an integer representing the screenspace coordinate of 
	 * 						the specified coordinate (measured in pixels)
	 */
	public static int v2ss(double coord, boolean isX, int screenHeight, 
			int screenWidth, double scale, PVec2 viewPos) {
		int scaled = (int) Math.round(coord * scale);
		int transpose = isX 
		? (screenWidth / 2) + (int) viewPos.x() 
		: (screenHeight / 2) + (int) viewPos.y();
		return scaled + transpose;

	}

	/**
	 * "Screen Space to Viewport" Converts a pair of screenspace coordinates to 
	 * viewport coordinates.
	 * @param  x 			x coordinate (in pixels) to convert
	 * @param  y 			y coordinate (in pixels) to convert
	 * @param  screenHeight height (in pixels) of the screenspace window
	 * @param  screenWidth  width (in pixels) of the screenspace window
	 * @param  scale 		screenspace zoom multiplier
	 * @param  viewPos 		coordinates of the origin of the viewport
	 * @return				an array of 2 doubles representing the viewport 
	 * 						coordinates of the specified coordinates in the 
	 * 						order {x, y}
	 */
	public static double[] ss2v(int x, int y, int screenHeight, int screenWidth, 
			double scale, PVec2 viewPos) {
		return new double[]{
			ss2v(x, true, screenHeight, screenWidth, scale, viewPos),
			ss2v(y, false, screenHeight, screenWidth, scale, viewPos)};

	}

	/**
	 * "Screen Space to Viewport" Converts single screenspace coordinate to a
	 * viewport coordinate.
	 * @param  coord 		screenspace coordinate (in pixels) to convert
	 * @param  isX   		specifies if the input coordinate represents x or y
	 * @param  screenHeight height (in pixels) of the screenspace window
	 * @param  screenWidth  width (in pixels) of the screenspace window
	 * @param  scale 		screenspace zoom multiplier
	 * @param  viewPos 		coordinates of the origin of the viewport
	 * @return				a double representing the viewport coordinate of the 
	 * 						specified coordinate
	 */
	public static double ss2v(int coord, boolean isX, int screenHeight, 
			int screenWidth, double scale, PVec2 viewPos) {
		double transpose = isX
		? ((double) screenWidth / 2) + viewPos.x()
		: ((double) screenHeight / 2) + viewPos.y();
		return ((double) (coord - transpose)) / scale;

	}
}
