package com.ang.peLib.graphics;

import com.ang.peLib.hittables.PHitRecord;

public class PFlatMask {
	public int[][] ffMask;
	public int[][] bfMask;
	public int[][] floorMask;
	public int[][] ceilingMask;
	private int height;

	public PFlatMask(int width, int height) {
		ffMask = new int[width][2];
		bfMask = new int[width][2];
		floorMask = new int[width][2];
		ceilingMask = new int[width][2];
		this.height = height;
		for (int i = 0; i < ceilingMask.length; i++) {
			ceilingMask[i][0] = height - 1;
		}
	}

	public void saveToBoundingMasks(double elevation, PHitRecord hitRec, int x, int[] bounds) {
		if (hitRec.isBackface()) {
			bfMask[x][0] = bounds[0];
			bfMask[x][1] = bounds[1];
			if (hitRec.getFloorHeight() > elevation) {
				floorMask[x][0] = bounds[0];
			}
			if (hitRec.getCeilingHeight() < elevation) {
				ceilingMask[x][1] = bounds[1];
			}
		} else {
			ffMask[x][0] = bounds[0];
			ffMask[x][1] = bounds[1];
			if (hitRec.getFloorHeight() > elevation) {
				floorMask[x][1] = bounds[0];
			}
			if (hitRec.getCeilingHeight() < elevation) {
				ceilingMask[x][0] = bounds[1];
			} else {
				ceilingMask[x][0] = height - 1;
			}
		}
	}
}
