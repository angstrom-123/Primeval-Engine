package com.ang.peLib.utils;

import com.ang.peLib.debug.PVisualizer; // debug
import com.ang.peLib.hittables.PHitRecord;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.maths.PInterval;
import com.ang.peLib.maths.PRay;
import com.ang.peLib.maths.PVec2;

public class PConvexDecomposer {
	// private PVisualizer visualizer = new PVisualizer(); // debug
	private PSectorWorld world;

	public PConvexDecomposer(PSectorWorld world) {
		this.world = world;
	}

	public PSectorWorld decompose() {
		while (true) {
			boolean found = false;
			for (int i = 0; i < world.getSectors().length; i++) {
				found = found || resolveSector(i);
			}
			if (!found) break;

		}
		// visualizer.visualize(world); // debug
		return world;

	}

	private boolean resolveSector(int sectorIndex) {
		int reflexIndex = findReflex(world.getSector(sectorIndex));
		if (reflexIndex == -1) return false;

		PSector[] newSecs = resolveReflex(world.getSector(sectorIndex), reflexIndex);
		if (newSecs == null) {
			System.err.println("Failed to resolve sector at " + sectorIndex);
			return false;

		}
		resolveSector(world.addSector(newSecs[0]));
		resolveSector(world.addSector(newSecs[1]));
		world.removeSectorAt(sectorIndex);
		return true;

	}

	private PSector[] resolveReflex(PSector sec, int reflexIndex) {
		int[] indices = getBisectionIndices(sec, reflexIndex);
		if (indices[1] == -1) {
			System.err.println("Failed to resolve reflex corner at index " + reflexIndex);
			return null;

		}
		return bisect(sec, indices[0], indices[1]);

	}

	private PSector[] bisect(PSector sec, int bisecStartIndex, int bisecEndIndex) {
		sec.setAsPortal(bisecStartIndex, true);
		sec.setAsPortal(bisecEndIndex, true);
		PVec2 bisecStart = sec.getCorner(bisecStartIndex);
		PVec2 bisecEnd = sec.getCorner(bisecEndIndex);
		int[] lIndices = new int[sec.getCorners().length];
		int[] rIndices = new int[sec.getCorners().length];
		int lHead = 0;
		int rHead = 0;
		for (int i = 0; i < sec.getCorners().length; i++) {
			if (i == bisecStartIndex) { 
				lIndices[lHead++] = i;
				rIndices[rHead++] = i;
				continue;

			}
			if (i == bisecEndIndex) { 
				lIndices[lHead++] = i;
				rIndices[rHead++] = i;
				continue;

			}
			if (isLeft(bisecStart, bisecEnd, sec.getCorner(i)))	lIndices[lHead++] = i;
			else rIndices[rHead++] = i;
		}
		PSector left = buildSector(sec, PArrays.reduceArray(lIndices, lHead));
		PSector right = buildSector(sec, PArrays.reduceArray(rIndices, rHead));
		return new PSector[]{left, right};

	}

	private PSector buildSector(PSector originalSec, int[] cornerIndices) {
		PVec2[] corners = new PVec2[cornerIndices.length];
		int[] portalIndices = new int[cornerIndices.length];
		int head = 0;
		for (int i = 0; i < cornerIndices.length; i++) {
			corners[i] = originalSec.getCorner(cornerIndices[i]).copy();
			if (originalSec.isPortal(cornerIndices[i])) portalIndices[head++] = i;
		}
		PSector out = new PSector(corners, PArrays.reduceArray(portalIndices, head));
		out.setHeight(originalSec.getFloorHeight(), originalSec.getCeilingHeight());
		return out;
	}

	/**
	 * Calculates the index of a corner in the specified sector to bisect to 
	 * given the starting index of the bisection.
	 * A ray is cast along the average normal of the corner at startIndex to find 
	 * the bisection point. If there is an existing corner in the sector near the 
	 * bisection point, this corner is chosen; else, a new corner is inserted.
	 * The index of this chosen / inserted corner is returned.
	 * @param  sec		  sector to be bisected
	 * @param  startIndex index of the corner in the sector where the bisection 
	 * 					  should start
	 * @return 			  index of the corner chosen / inserted to bisect to
	 */
	private int[] getBisectionIndices(PSector sec, int startIndex) {
		PVec2 dir = findAverageNormal(sec, startIndex).neg();
		PRay r = new PRay(sec.getCorner(startIndex), dir);
		double bestT = Double.MAX_VALUE;
		PVec2 intersection = null;
		int hitEdgeIndex = -1;
		for (int i = 0; i < sec.getWalls().length; i++) {
			PHitRecord hitRec = new PHitRecord();
			if (sec.getWalls()[i].hit(r, PInterval.universe(), hitRec)) {
				if (hitRec.getT() < 1.0E-7) continue;

				bestT = Math.min(bestT, hitRec.getT());
				intersection = r.at(hitRec.getT());
				hitEdgeIndex = i;
			}
		}
		if (intersection == null) return new int[]{startIndex, -1};

		int selectedIndex;
		int closeIndex = findCloseIndex(sec, intersection);
		if (closeIndex == -1) {
			selectedIndex = sec.insertCornerAfter(hitEdgeIndex);
			sec.getCorners()[selectedIndex] = intersection;
			if (startIndex > selectedIndex) startIndex++;
		} else selectedIndex = closeIndex;
		return new int[]{startIndex, selectedIndex};

	}

	/**
	 * Returns the index of a corner that is close to the given point.
	 * @param  sec sector to find close corners in 
	 * @param  p   point to find a corner close to
	 * @return     index in the sector of the first close corner, -1 if there are none
	 */
	private int findCloseIndex(PSector sec, PVec2 p) {
		final double epsilon = 0.1;
		int possible = -1;
		for (int i = 0; i < sec.getCorners().length; i++) {
			if ((sec.getCorner(i).sub(p)).length() < epsilon) {
				if (!sec.isPortal(i)) return i;
				possible = i;
			}
		}
		return possible;

	}

	/**
	 * Returns the index of the first reflex corner in the sector.
	 * @param  sec sector to find reflex corners in 
	 * @return 	   index of the first reflex corner, else -1
	 */
	private int findReflex(PSector sec) {
		for (int i = 0; i < sec.getCorners().length; i++) {
			if (isReflex(sec, i)) return i;

		}
		return -1;
		
	}

	/**
	 * Checks if a corner in a sector has a reflex (<&gt>180) internal angle.
	 * @param  sec 	 sector containing the relevant corner
	 * @param  index index in the sector of the relevant corner
	 * @return 		 {@code true} if the internal angle is reflex, else {@code false}
	 */
	private boolean isReflex(PSector sec, int index) {
		int next = (index == sec.getCorners().length - 1) ? 0 : index + 1;
		int prev = (index == 0) ? sec.getCorners().length - 1 : index - 1;
		PVec2 toPrev = sec.getCorner(prev).sub(sec.getCorner(index));
		PVec2 toNext = sec.getCorner(next).sub(sec.getCorner(index));
		PVec2 normal = new PVec2(toPrev.y(), -toPrev.x());
		double theta = Math.acos(PVec2.dot(normal, toNext) 
								 / (normal.length() * toNext.length()));
		// adding offset makes it likely to detect exactly 180 degree angles as relfex
		if (theta > (Math.PI / 2.0) + 1E-7) return true;

		return false;

	}

	/**
	 * Checks if a point is on the left or right of a line.
	 * @param  a one point on the line 
	 * @param  b another point on the line
	 * @param  p the point to check if it is left or right
	 * @return   {@code true} if the point is on the left of the line, else {@code false}
	 */
	private boolean isLeft(PVec2 a, PVec2 b, PVec2 p) {
		double m = (a.y() - b.y()) / (a.x() - b.x());
		if (Math.abs(a.x() - b.x()) < 1.0E-7) m = 1.0;
		if (Math.abs(a.y() - b.y()) < 1.0E-7) m = 0.0;
		double c = a.y() - m * a.x();
		if (m != 0.0) {
			double x = (p.y() - c) / m;
			return p.x() < x; 

		} else return p.y() < c; 
	}

	/**
	 * Finds the average normal vector of edges surrounding a corner.
	 * @param  sec 	 sector containing the relevant corners
	 * @param  index index in the sector of the relevant corner
	 * @return 		 average normal vector of surrounding edges
	 */
	private PVec2 findAverageNormal(PSector sec, int index) {
		int next = (index == sec.getCorners().length - 1) ? 0 : index + 1;
		int prev = (index == 0) ? sec.getCorners().length - 1 : index - 1;
		PVec2 normal0 = findNormal(sec, index, next);
		PVec2 normal1 = findNormal(sec, prev, index);
		PVec2 out = (normal0.add(normal1)).div(2.0);
		return out;

	}

	/**
	 * Finds the normal vector of an edge (between 2 corners)
	 * @param  sec 	  sector containing the relevant corners
	 * @param  index0 index in the sector of the first corner
	 * @param  index1 index in the sector of the second corner
	 * @return 		  normal vector of the edge
	 */
	private PVec2 findNormal(PSector sec, int index0, int index1) {
		PVec2[] corners = sec.getCorners();
		double dx = corners[index1].x() - corners[index0].x();
		double dy = corners[index1].y() - corners[index0].y();
		return  (new PVec2(dy, -dx)).unitVector();

	}
}
