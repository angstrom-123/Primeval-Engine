package com.ang.peLib.hittables;

import com.ang.peLib.graphics.PColour;

/**
 * Stores information about a collision between a ray and a hittable.
 * @see PEdge
 * @see PSector
 */
public class PHitRecord {
	private double t; // distance to the hit
	private PColour colour; // colour of the surface at the hit point
	private double floorHeight; // floor height of edge at hit
	private double ceilingHeight; // ceiling height of edge at hit

	/**
	 * {@inheritDoc}
	 */
	public PHitRecord copy() {
		PHitRecord temp = new PHitRecord();
		temp.setT(t);
		temp.setColour(colour);
		temp.setFloor(floorHeight);
		temp.setCeiling(ceilingHeight);
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
	 * Returns the colour at the hit point stored.
	 * @return colour at the hit stored in this record
	 * @see    com.ang.peLib.graphics.PColour 
	 */
	public PColour getColour() {
		return colour;

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
	 * Sets the distance to the hit for this record.
	 * @param t the distance to the hit
	 */
	public void setT(double t) {
		this.t = t;
	}

	/**
	 * Sets the colour of the surface at the hit point.
	 * @param colour the colour of the surface at the hit point
	 * @see			 com.ang.peLib.graphics.PColour
	 */
	public void setColour(PColour colour) {
		this.colour = colour;
	}

	/**
	 * Sets the floor height of the sector at the hit point.
	 * @param floorHeight the height of the sector's floor at the hit point
	 * @see				  PSector
	 */
	public void setFloor(double floorHeight) {
		this.floorHeight= floorHeight;
	}

	/**
	 * Sets the ceiling height of the sector at the hit point.
	 * @param ceilingHeight the height of the sector's ceiling at the hit point
	 * @see				    PSector
	 */
	public void setCeiling(double ceilingHeight) {
		this.ceilingHeight = ceilingHeight;
	}
}
