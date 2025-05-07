package com.ang.peLib.hittables;

import com.ang.peLib.utils.*;

import com.ang.peLib.graphics.PColour;
import com.ang.peLib.maths.*;

/**
 * Hittable object defining a sector.
 * A sector consists of {@link PEdge}s that are all connected at their ends.
 * Sectors are always enclosed and have a constant floor and ceiling height.
 * Sectors can have portals which are edges with no collision and no visibility.
 * @see PEdge
 */
public class PSector extends PCopyable {
	private PEdge[] walls;
	private PVec2[] corners;
	private int[] portalIndices;
	private double floorHeight;
	private double ceilingHeight;
	// TODO: implement light level
	private double lightLevel = 1.0; 

	/**
	 * Constructor for a sector from its corners and the portal data.
	 * @param corners		all of the corners of the sector, defined in order.
	 * 						Sectors are always enclosed, the final corner is 
	 * 						automatically joined to the first, it does not have to 
	 * 						be passed in twice.
	 * @param portalIndices indices into the corners array that define the bounds
	 * 						of a portal edge (edge with no collision or visiblity)
	 */
	public PSector(PVec2[] corners, int[] portalIndices) {
		this.corners = corners;
		// this.portalIndices = portalIndices;
		this.portalIndices = cleanPortalIndices(portalIndices);
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

	/**
	 * Removes any invalid portal indices (less than 0) from provided indices. 
	 * @param  portalIndices array of indices to clean
	 * @return 				 a copy of provided indices with negative indices removed
	 */
	private int[] cleanPortalIndices(int[] portalIndices) {
		int[] out = new int[portalIndices.length];
		int length = 0;
		for (int i = 0; i < portalIndices.length; i++) {
			if (portalIndices[i] >= 0) {
				out[i] = portalIndices[i];
				length = i + 1;
			}
		}
		return PArrays.reduceArray(out, length);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PSector copy() {
		return new PSector(PArrays.copy(corners, PVec2.class), portalIndices.clone());

	}

	/**
	 * Sets the floor and ceiling height for the sector.
	 * @param floorHeight   the height of the floor to set
	 * @param ceilingHeight the height of the ceiling to set
	 */
	public void setHeight(double floorHeight, double ceilingHeight) {
		this.floorHeight = floorHeight;
		this.ceilingHeight = ceilingHeight;
	}

	/**
	 * Sets the light level for the sector.
	 * Currently light level is not implemented.
	 * @param lightLevel the level of light to set for the sector
	 */
	public void setLightLevel(double lightLevel) {
		this.lightLevel = lightLevel;
	}

	/**
	 * Returns the height of the sector's floor.
	 * @return the sector's floor height
	 */
	public double getFloorHeight() {
		return floorHeight;

	}

	/**
	 * Returns the height of the sector's ceiling.
	 * @return the sector's ceiling height
	 */
	public double getCeilingHeight() {
		return ceilingHeight;

	}

	/**
	 * Returns the light level of the sector.
	 * @return the sector's light level
	 */
	public double getLightLevel() {
		return lightLevel;

	}

	/**
	 * Returns all of the walls that make up the sector.
	 * @return the walls that make up the sector
	 * @see    PEdge
	 */
	public PEdge[] getWalls() {
		return walls;

	}

	/**
	 * Returns all of the corners that make up the sector.
	 * @return the corners that make up the sector
	 * @see    com.ang.peLib.maths.PVec2
	 */
	public PVec2[] getCorners() {
		return corners;

	}

	/**
	 * Returns all of the indices of corners making up portal edges.
	 * @return the indices of the corners that bounds the portal edges
	 * @see    PEdge
	 */
	public int[] getPortalIndices() {
		return portalIndices;

	}

	/**
	 * Determines if a given pair of indices into the sector's corners make up a 
	 * portal edge.
	 * @param  indexOne the first index
	 * @param  indexOne the second index
	 * @return		    {@code true} if the corners at the specified indices make 
	 * 					a portal else {@code false}
	 * @see 			PEdge
	 */
	private boolean isPortal(int indexOne, int indexTwo) {
		boolean foundOne = false;
		boolean foundTwo = false;
		for (int i : portalIndices) {
			if (foundOne && foundTwo) {
				return true;

			}
			if (i == indexOne) {
				foundOne = true;
			} else if (i == indexTwo) {
				foundTwo = true;
			}
		}
		return false;

	}

	/**
	 * Determines if a ray intersects with the sector within an interval of distance.
	 * @param  r			the {@link com.ang.peLib.maths.PRay} to look for hits with
	 * @param  tInterval	bounds on the distance within which to search for hits
	 * @param  rec			the {@link PHitRecord} to record the hit to
	 * @return 				{@code true} if an intersection is found, else {@code false}
	 * @see 				com.ang.peLib.maths.PRay
	 * @see 				com.ang.peLib.maths.PInterval
	 * @see					PEdge
	 * @see					PHitRecord
	 */
	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		boolean didHit = false;
		double closestHit = Double.MAX_VALUE;
		PHitRecord tempRec = new PHitRecord();
		for (int i = 0; i < walls.length; i++) {
			PEdge w = walls[i];
			PInterval bounds = new PInterval(tInterval.getMin(), closestHit);
			if (w.hit(r, bounds, tempRec)) {
				if ((tempRec.getT() >= 0.0) && (tempRec.getT() < tInterval.getMax())) {
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

	/**
	 * Finds all intersections between a ray and the sector within an interval of distance.
	 * @param  r			the {@link com.ang.peLib.maths.PRay} to look for hits with
	 * @param  tInterval	bounds on the distance within which to search for hits
	 * @return 				an array of {@link PHitRecord}s holding information
	 * 						about each hit
	 * @see 				com.ang.peLib.maths.PRay
	 * @see 				com.ang.peLib.maths.PInterval
	 * @see					PEdge
	 * @see					PHitRecord
	 */
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

	/**
	 * Returns a string representation of the sector for debugging.
	 * @return the string representation of the sector
	 */
	@Override
	public String toString() {
		String out = "Floor: " + floorHeight + "\n"
				+ "Ceiling: " + ceilingHeight + "\n";
		for (PVec2 v : corners) {
			out += v.toString() + "\n";
		}
		return out;

	}
}
