package com.ang.peLib.graphics;

import com.ang.peLib.hittables.PHitRecord;

/**
 * Class for representing a floor or ceiling mask for a sector.
 */
public class PFlatMask {
	public int[][] floor;
	public int[][] ceiling;

	/**
	 * Constructs a new flatmask with a width and height (should be equal to 
	 * screen width and heigh).
	 * @param width  the width of the mask
	 * @param height the height of the mask
	 */
	public PFlatMask(int width, int height) {
		floor = new int[width][2];
		ceiling = new int[width][2];
		for (int i = 0; i < width; i++) {
			floor[i][0] = height - 1;
			floor[i][1] = 0;
			ceiling[i][0] = height - 1;
			ceiling[i][1] = 0;
		}
	}

	/**
	 * Saves the bounds of a vertical column of pixels to the current sector's 
	 * flatmask.
	 * @param elevation the current elevation of the camera 
	 * @param hitRec    the hitRecord representing the intersecion that is being masked
	 * @param x			x coordinate of the pixel being saved 
	 * @param bounds    the upper and lower pixel coordinates of the column at the x point
	 */
	public void saveToBoundingMasks(double elevation, PHitRecord hitRec, int x, int[] bounds) {
		if (hitRec.isBackface()) {
			if (hitRec.getFloorHeight() < elevation) {
				floor[x][1] = bounds[0];
			} else {
				floor[x][0] = bounds[0];
			}
			if (hitRec.getCeilingHeight() > elevation) {
				ceiling[x][0] = bounds[1];
			} else {
				ceiling[x][1] = bounds[1];
			}
		} else {
			if (hitRec.getFloorHeight() < elevation) {
				floor[x][0] = bounds[0];
			} else {
				floor[x][1] = bounds[0];
			}
			if (hitRec.getCeilingHeight() > elevation) {
				ceiling[x][1] = bounds[1];
			} else {
				ceiling[x][0] = bounds[1];
			}
		}
	}
}
