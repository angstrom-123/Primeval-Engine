package com.ang.peLib.maths;

import com.ang.peLib.utils.PCopyable;

public class PVec2 extends PCopyable {
	private double x = 0.0;
	private double y = 0.0;

	public PVec2() {}

	@Override
	@SuppressWarnings("unchecked")
	public PVec2 copy() {
		return new PVec2(x, y);

	}

	public PVec2(double[] vals) {
		if (vals.length != 2) {
			throw new NullPointerException();
		
		}
		this.x = vals[0];
		this.y = vals[1];
	}

	public PVec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double x() {
		return this.x;

	}

	public double y() {
		return this.y;

	}

	@Override
	public String toString() {
		return ("x: " + x + "\n"
				+ "y: " + y);

	}

	public PVec3 toVec3() {
		return new PVec3(x, y, 0.0);

	}	

	public void setAxis(int axis, double val) {
		switch (axis) {
		case 0:
			x = val;
			break;

		case 1:
			y = val;
			break;

		default:
			break;

		}
	}

	public PVec2 add(PVec2 v) {
		return new PVec2(this.x + v.x(), this.y + v.y());

	}

	public PVec2 sub(PVec2 v) {
		return new PVec2(this.x - v.x(), this.y - v.y());

	}

	public PVec2 mul(double t) {
		return new PVec2(this.x * t, this.y * t);

	}

	public PVec2 div(double t) {
		return mul(1 / t);

	}

	public PVec2 neg() {
		return new PVec2(-x, -y);

	}

	public double lengthSquared() {
		return (this.x * this.x) + (this.y * this.y);

	}

	public double length() {
		return Math.sqrt(lengthSquared());

	}

	public PVec2 unitVector() {
		return div(length());

	}

	public boolean nearZero() {
		double min = 1E-8;
		return (Math.abs(this.x) < min)	&& (Math.abs(this.y) < min);

	}

	public double axis(int axis) {
		switch (axis) {
		case 0:
			return x;

		case 1:
			return y;

		default:
			return 0.0;

		}
	}

	public static double cross(PVec2 u, PVec2 v) {
		return (u.x() * v.y()) - (u.y() * v.x());

	}

	public static double dot(PVec2 u, PVec2 v) {
		return (u.x() * v.x()) + (u.y() * v.y()); 

	}
}
