package com.ang.peLib.maths;

public class PVec3 {
	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	public PVec3() {}

	public PVec3 copy() {
		return new PVec3(x, y, z);

	}

	public PVec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double x() {
		return this.x;

	}

	public double y() {
		return this.y;

	}

	public double z() {
		return this.z;

	}

	@Override
	public String toString() {
		return ("x: " + x + "\n"
				+ "y: " + y + "\n"
				+ "z: " + z + "\n");

	}

	public PVec2 toVec2() {
		return new PVec2(x, y);

	}

	public void setAxis(int axis, double val) {
		switch (axis) {
		case 0:
			x = val;
			break;

		case 1:
			y = val;
			break;
		case 2:
			z = val;
			break;

		default:
			break;

		}
	}

	public PVec3 add(PVec3 v) {
		return new PVec3(x + v.x(), y + v.y(), z + v.z());

	}

	public PVec3 add(PVec2 v) {
		return new PVec3(x + v.x(), y + v.y(), z);

	}

	public PVec3 sub(PVec3 v) {
		return new PVec3(x - v.x(), y - v.y(), z - v.z());

	}

	public PVec3 sub(PVec2 v) {
		return new PVec3(x - v.x(), y - v.y(), z);

	}

	public PVec3 mul(double t) {
		return new PVec3(x * t, y * t, z * t);

	}

	public PVec3 div(double t) {
		return mul(1 / t);

	}

	public PVec3 neg() {
		return new PVec3(-x, -y, -z);

	}

	//public double minAxis() {
	//	double min = Global.INFINITY;
	//	for (int i = 0; i < 3; i++) {
	//		if (axis(i) > min) {
	//			min = axis(i);
	//		}
	//	}
	//	return min;
	//
	//}
	//
	//public double maxAxis() {
	//	double max = -Global.INFINITY;
	//	for (int i = 0; i < 3; i++) {
	//		if (axis(i) > max) {
	//			max = axis(i);
	//		}
	//	}
	//	return max;
	//
	//}

	public double lengthSquared() {
		return (x * x) + (y * y) + (z * z);

	}

	public double length() {
		return Math.sqrt(lengthSquared());

	}

	public PVec3 unitVector() {
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

		case 2:
			return z;

		default:
			return 0.0;

		}
	}

	public static PVec3 min(PVec3 u, PVec3 v) {
		PVec3 out = new PVec3();
		for (int i = 0; i < 3; i++) {
			out.setAxis(i, Math.min(u.axis(i), v.axis(i)));
		}
		return out;

	}

	public static PVec3 max(PVec3 u, PVec3 v) {
		PVec3 out = new PVec3();
		for (int i = 0; i < 3; i++) {
			out.setAxis(i, Math.max(u.axis(i), v.axis(i)));
		}
		return out;

	}

	public static double dot(PVec3 u, PVec3 v) {
		return (u.x() * v.x()) + (u.y() * v.y()) + (u.z() * v.z()); 

	}

	public static PVec3 cross(PVec3 u, PVec3 v) {
		return new PVec3(u.y() * v.z() - u.z() * v.y(),
				u.z() * v.x() - u.x() * v.z(),
				u.x() * v.y() - u.y() * v.x());

	}
}
