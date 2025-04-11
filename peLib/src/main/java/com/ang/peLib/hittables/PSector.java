package com.ang.peLib.hittables;

import com.ang.peLib.utils.*;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.maths.*;

public class PSector extends PCopyable {
	private PEdge[] walls;
	private PVec2[] corners;
	private int[] portalIndices;
	private double floorHeight;
	private double ceilingHeight;
	// TODO: implement light level
	private double lightLevel = 1.0; 

	public PSector(PVec2[] corners, int[] portalIndices) {
		this.corners = corners;
		this.portalIndices = portalIndices;
		walls = new PEdge[corners.length];
		int head = 0;
		for (int i = 0; i < corners.length; i++) {
			PEdge wall;
			int nextI = (i < corners.length - 1) ? i + 1 : 0;
			wall = new PEdge(corners[i], corners[nextI], new PColour(1.0, 1.0, 1.0));
			if (isPortal(i, nextI)) {
				wall.setAsPortal();
			}
			walls[head++] = wall;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PSector copy() {
		return new PSector(PArrays.copy(corners, PVec2.class), portalIndices);

	}

	public void setHeight(double floorHeight, double ceilingHeight) {
		this.floorHeight = floorHeight;
		this.ceilingHeight = ceilingHeight;
	}

	public void setLightLevel(double lightLevel) {
		this.lightLevel = lightLevel;
	}

	public double getFloorHeight() {
		return floorHeight;

	}

	public double getCeilingHeight() {
		return ceilingHeight;

	}

	public double getLightLevel() {
		return lightLevel;

	}

	public PEdge[] getWalls() {
		return walls;

	}

	public PVec2[] getCorners() {
		return corners;

	}

	public int[] getPortalIndices() {
		return portalIndices;

	}

	private boolean isPortal(int indexOne, int indexTwo) {
		boolean foundOne = false;
		boolean foundTwo = false;
		for (int i : portalIndices) {
			if (!foundOne || !foundTwo) {
				if (i == indexOne) {
					foundOne = true;
				}
				if (i == indexTwo) {
					foundTwo = true;
				}
			}
		}
		return foundOne && foundTwo;

	}

	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		boolean didHit = false;
		double closestHit = Double.MAX_VALUE;
		PHitRecord tempRec = new PHitRecord();
		for (int i = 0; i < walls.length; i++) {
			PEdge w = walls[i];
			PInterval bounds = new PInterval(tInterval.min(), closestHit);
			if (w.hit(r, bounds, tempRec)) {
				if ((tempRec.getT() >= 0.0) && (tempRec.getT() < tInterval.max())) {
					didHit = true;
					closestHit = tempRec.getT();
					rec.setT(tempRec.getT());
					rec.setColour(tempRec.getColour());
					rec.setFloor(ceilingHeight);
					rec.setCeiling(ceilingHeight);
				}
			}
		}
		return didHit;

	}

	public PHitRecord[] allHits(PRay r, PInterval tInterval) {
		PHitRecord[] hits = new PHitRecord[walls.length];
		int head = 0;
		for (int i = 0; i < walls.length; i++) {
			PEdge w = walls[i];
			PHitRecord tempRec = new PHitRecord();
			if (w.hit(r, tInterval.copy(), tempRec)) {
				tempRec.setFloor(floorHeight);
				tempRec.setCeiling(ceilingHeight);
				hits[head++] = tempRec;
			}
		}
		return PArrays.reduceArray(hits, head, PHitRecord.class);

	}

	@Override
	public String toString() {
		return ("Floor: " + floorHeight + "\n"
				+ "Ceiling: " + ceilingHeight + "\n"
				+ super.toString());

	}
}
