package com.ang.peLib.graphics;

import com.ang.peLib.hittables.PHitRecord;

public class PFlatMask {
	public int[][] floor;
	public int[][] ceiling;

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

		// if (hitRec.isBackface()) {
		// 	floor[x][1] = bounds[0];
		// 	ceiling[x][0] = bounds[1];
		// } else {
		// 	floor[x][0] = bounds[0];
		// 	ceiling[x][1] = bounds[1];
		// }

		// if (hitRec.isBackface()) {
		// 	floor[x][0] = bounds[0];
		// 	ceiling[x][1] = bounds[1];
		// } else {
		// 	floor[x][1] = bounds[0];
		// 	ceiling[x][0] = bounds[1];
		// }
	}
}
