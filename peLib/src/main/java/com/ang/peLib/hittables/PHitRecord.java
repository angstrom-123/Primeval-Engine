package com.ang.peLib.hittables;

import com.ang.peLib.graphics.PColour;
import com.ang.peLib.utils.PCopyable;

/**
 * Stores information about a collision between a ray and a hittable.
 * @see PEdge
 * @see PSector
 */
public class PHitRecord extends PCopyable {
	private double t; // distance to the hit
	private PColour colour; // colour of the surface at the hit point
	private PColour upperColour;
	private PColour lowerColour;
	private double floorHeight; // floor height of edge at hit
	private double ceilingHeight; // ceiling height of edge at hit
	private boolean isBackface; // true if this hit is on the inside of a sector
	private boolean isPortal;
	private int sectorIndex;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PHitRecord copy() {
		PHitRecord temp = new PHitRecord();
		temp.setT(t);
		temp.setColour(colour);
		temp.setUpperColour(upperColour);
		temp.setLowerColour(lowerColour);
		temp.setFloor(floorHeight);
		temp.setCeiling(ceilingHeight);
		temp.setBackface(isBackface);
		temp.setPortal(isPortal);
		temp.setSectorIndex(sectorIndex);
		return temp;

	}

	/**
	 * Returns the distance to the hit stored.
	 * @return distance to the hit stored in this record
	 */
	public double getT() {
		return t;

	}

	/**
	 * Returns the main colour at the hit point stored.
	 * @return colour at the hit stored in this record
	 * @see    com.ang.peLib.graphics.PColour 
	 */
	public PColour getColour() {
		return colour;

	}

	/**
	 * Returns the upper colour at the hit point stored.
	 * @return upper colour at the hit stored in this record
	 * @see    com.ang.peLib.graphics.PColour 
	 */
	public PColour getUpperColour() {
		return upperColour;

	}

	/**
	 * Returns the lower colour at the hit point stored.
	 * @return lower colour at the hit stored in this record
	 * @see    com.ang.peLib.graphics.PColour 
	 */
	public PColour getLowerColour() {
		return lowerColour;

	}

	/**
	 * Returns the floor height at the hit point stored.
	 * @return floor height at the hit stored in this record
	 * @see    PSector
	 */
	public double getFloorHeight() {
		return floorHeight;

	}

	/**
	 * Returns the ceiling height at the hit point stored.
	 * @return ceiling height at the hit stored in this record
	 * @see    PSector
	 */
	public double getCeilingHeight() {
		return ceilingHeight;

	}

	/**
	 * Checks if the hitRecord hit a backface or a frontface.
	 * @return {@code true} if the hitRecord is for a backface, else {@code false}
	 */
	public boolean isBackface() {
		return isBackface;

	}

	/**
	 * Checks if the hitRecord hit a portal.
	 * @return {@code true} if this hitRecord is for a portal hit, else {@code false}
	 */
	public boolean isPortal() {
		return isPortal;

	}

	/**
	 * Returns the index of the sector that this hitRecord is for.
	 * @return index of the sector hit
	 * @see    com.ang.peLib.hittables.PSector
	 */
	public int getSectorIndex() {
		return sectorIndex;

	}

	/**
	 * Sets the distance to the hit for this record.
	 * @param t the distance to the hit
	 */
	public void setT(double t) {
		this.t = t;
	}

	/**
	 * Sets the main colour of the surface at the hit point.
	 * @param colour the colour of the surface at the hit point
	 * @see			 com.ang.peLib.graphics.PColour
	 */
	public void setColour(PColour colour) {
		this.colour = colour;
	}

	/**
	 * Sets the upper colour of the surface at the hit point.
	 * @param colour the colour of the surface at the hit point
	 * @see			 com.ang.peLib.graphics.PColour
	 */
	public void setUpperColour(PColour colour) {
		this.upperColour = colour;
	}

	/**
	 * Sets the lower colour of the surface at the hit point.
	 * @param colour the colour of the surface at the hit point
	 * @see			 com.ang.peLib.graphics.PColour
	 */
	public void setLowerColour(PColour colour) {
		this.lowerColour = colour;
	}

	/**
	 * Sets the floor height of the sector at the hit point.
	 * @param floorHeight the height of the sector's floor at the hit point
	 * @see				  PSector
	 */
	public void setFloor(double floorHeight) {
		this.floorHeight = floorHeight;
	}

	/**
	 * Sets the ceiling height of the sector at the hit point.
	 * @param ceilingHeight the height of the sector's ceiling at the hit point
	 * @see				    PSector
	 */
	public void setCeiling(double ceilingHeight) {
		this.ceilingHeight = ceilingHeight;
	}

	/**
	 * Sets the hit as a backface hit or a frontface hit.
	 * @param isBackface if the hit is a backface
	 */
	public void setBackface(boolean isBackface) {
		this.isBackface = isBackface;
	}

	/**
	 * Sets the hit as a portal hit or a normal hit.
	 * @param isPortal if the hit is a portal
	 */
	public void setPortal(boolean isPortal) {
		this.isPortal = isPortal;
	}

	/**
	 * Sets the index of the sector that the hit is recorded for.
	 * @param sectorIndex the index to set
	 */
	public void setSectorIndex(int sectorIndex) {
		this.sectorIndex = sectorIndex;
	}
}
