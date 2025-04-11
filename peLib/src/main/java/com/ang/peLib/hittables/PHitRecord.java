package com.ang.peLib.hittables;

import com.ang.peLib.graphics.PColour;

public class PHitRecord {
	private double t;
	private PColour colour;
	private double floorHeight;
	private double ceilingHeight;

	public PHitRecord() {}

	public PHitRecord copy() {
		PHitRecord temp = new PHitRecord();
		temp.setT(t);
		temp.setColour(colour);
		temp.setFloor(floorHeight);
		temp.setCeiling(ceilingHeight);
		return temp;

	}

	public double getT() {
		return t;

	}

	public PColour getColour() {
		return colour;

	}

	public double getFloorHeight() {
		return floorHeight;

	}

	public double getCeilingHeight() {
		return ceilingHeight;

	}

	public void setT(double t) {
		this.t = t;
	}

	public void setColour(PColour colour) {
		this.colour = colour;
	}

	public void setFloor(double floorHeight) {
		this.floorHeight= floorHeight;
	}

	public void setCeiling(double ceilingHeight) {
		this.ceilingHeight = ceilingHeight;
	}
}
