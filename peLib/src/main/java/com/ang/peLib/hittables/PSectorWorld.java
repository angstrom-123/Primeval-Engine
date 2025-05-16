package com.ang.peLib.hittables;

import com.ang.peLib.maths.*;
import com.ang.peLib.utils.*;

/**
 * Represents a level in the game as an array of sectors.
 * @see PSector
 */
public class PSectorWorld extends PCopyable {
	private PSector[] sectors;
	private int maxSectors;
	private int head = 0;

	/**
	 * Constructs a new sector world.
	 * @param maxSectors the maximum amount of sectors allowed in this world
	 */
	public PSectorWorld(int maxSectors) {
		this.maxSectors = maxSectors;
		sectors = new PSector[maxSectors];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PSectorWorld copy() {
		PSectorWorld temp = new PSectorWorld(maxSectors);
		for (int i = 0; i < head; i++) {
			temp.addSector(sectors[i].copy());
		}
		return temp;

	}
	
	/**
	 * Adds a new sector the the world.
	 * @param sec the sector to add
	 * @see		  PSector
	 */
	public void addSector(PSector sec) {
		if (head < maxSectors) {
			sectors[head++] = sec;
		} else {
			System.out.println("Failed to add sector to world");
		}
	}

	public void removeSectorAt(int sectorIndex) {
		for (int i = sectorIndex; i < head; i++) {
			sectors[i] = (i + 1 >= sectors.length) ? null : sectors[i + 1];
		}
		head--;
	}

	/**
	 * Returns the maximum allowed sectors in this world.
	 * @return the maximum amount of sectors specified in constructor
	 * @see    #PSectorWorld(int)
	 */
	public int getMaxSectors() {
		return maxSectors;

	}

	/**
	 * Returns all sectors in this world
	 * @return an array of all the sectors in this world
	 * @see    PSector
	 */
	public PSector[] getSectors() {
		return (PSector[]) PArrays.reduceArray(sectors, head, PSector.class);

	}

	public PSector getSector(int i) {
		if ((i >= head) || (i < 0)) return null;

		return sectors[i];

	}

	/**
	 * Determines if a ray intersects with a sector in the world within an interval of distance.
	 * @param  r			the {@link com.ang.peLib.maths.PRay} to look for hits with
	 * @param  tInterval	bounds on the distance within which to search for hits
	 * @param  rec			the {@link PHitRecord} to record the hit to
	 * @return 				{@code true} if an intersection is found, else {@code false}
	 * @see 				com.ang.peLib.maths.PRay
	 * @see 				com.ang.peLib.maths.PInterval
	 * @see					PSector
	 * @see					PEdge
	 * @see					PHitRecord
	 */
	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		boolean didHit = false;
		double closestHit = Double.MAX_VALUE;
		PHitRecord tempRec = new PHitRecord();
		for (int i = 0; i < head; i++) {
			PSector sec = sectors[i];
			PInterval bounds = new PInterval(tInterval.getMin(), closestHit);
			if (sec.hit(r, bounds, tempRec)) {
				if ((tempRec.getT() >= 0.0) && (tempRec.getT() < tInterval.getMax())) {
					didHit = true;
					closestHit = tempRec.getT();
					rec.setT(tempRec.getT());
					rec.setColour(tempRec.getColour());
					rec.setFloor(tempRec.getFloorHeight());
					rec.setCeiling(tempRec.getCeilingHeight());
				}
			}
		}
		return didHit;

	}

	/**
	 * Finds all intersections between a ray and the world within an interval of distance.
	 * @param  r			the {@link com.ang.peLib.maths.PRay} to look for hits with
	 * @param  tInterval	bounds on the distance within which to search for hits
	 * @return 				an array of {@link PHitRecord}s holding information
	 * 						about each hit
	 * @see 				com.ang.peLib.maths.PRay
	 * @see 				com.ang.peLib.maths.PInterval
	 * @see					PSector
	 * @see					PEdge
	 * @see					PHitRecord
	 */
	public PHitRecord[] allHits(PRay r, PInterval tInterval) {
		PHitRecord[] hits = new PHitRecord[100];
		int hitsHead = 0;
		for (int i = 0; i < head; i++) {
			PSector sec = sectors[i];
			for (PHitRecord rec : sec.allHits(r, tInterval.copy())) {
				if ((rec.getT() >= 0.0) & (rec.getT() < tInterval.getMax())) {
					hits[hitsHead++] = rec;
				}
			}
		}
		return (PHitRecord[]) PArrays.reduceArray(hits, hitsHead, PHitRecord.class);

	}

	/**
	 * Returns a string representation of the world for debugging.
	 * @return the string representation of the world
	 */
	@Override 
	public String toString() {
		String out = "";
		for (int i = 0; i < head; i++) {
			out += "(\n" + sectors[i].toString() + "\n)";
		}
		return out;

	}
}
