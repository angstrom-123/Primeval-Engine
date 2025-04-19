package com.ang.peLib.maths;

import com.ang.peLib.utils.PCopyable;

/**
 * Represents a 3 dimensional vector.
 */
public class PVec3 extends PCopyable {
	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	/**
	 * Default constructor.
	 */
	public PVec3() {}

	/**
	 * Constructs the vector using 3 doubles.
	 * @param x the x value
	 * @param y the y value
	 * @param z the z value
	 */
	public PVec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PVec3 copy() {
		return new PVec3(x, y, z);

	}

	/**
	 * Returns the x value.
	 * @return the x value
	 */
	public double x() {
		return this.x;

	}

	/**
	 * Returns the y value.
	 * @return the y value
	 */
	public double y() {
		return this.y;

	}

	/**
	 * Returns the z value.
	 * @return the z value
	 */
	public double z() {
		return this.z;

	}

	/**
	 * Returns a copy converted to a 2 dimensional vector.
	 * The 3rd (z) axis is omitted to allow for the conversion.
	 * @return the 2D vector
	 * @see    PVec2
	 */
	public PVec2 toVec2() {
		return new PVec2(x, y);

	}

	/**
	 * Sets a given axis.
	 * @param axis the index of the axis to set (0:x, 1:y, 2:z)
	 * @param val  the value to assign to the axis
	 */
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

	/**
	 * Returns a given axis
	 * @param  axis the index of the axis to return (0:x, 1:y)
	 * @return 		the value of the vector in the given axis, default to 0.0
	 */
	public double getAxis(int axis) {
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

	/**
	 * Returns a copy of the vector with another vector added to it.
	 * @param  v the vector to add
	 * @return   the copy with the new vector added to it
	 */
	public PVec3 add(PVec3 v) {
		return new PVec3(x + v.x(), y + v.y(), z + v.z());

	}

	/**
	 * Returns a copy of the vector with another vector added to it.
	 * Override for 2D vector addition, the z axis of the specified vector is 
	 * implicitly 0.0.
	 * @param  v the vector to add
	 * @return   the copy with the new vector added to it
	 */
	public PVec3 add(PVec2 v) {
		return new PVec3(x + v.x(), y + v.y(), z);

	}

	/**
	 * Returns a copy of the vector with another vector subtracted from it.
	 * @param  v the vector to subtract
	 * @return   the copy with the new vector subtracted from it
	 */
	public PVec3 sub(PVec3 v) {
		return new PVec3(x - v.x(), y - v.y(), z - v.z());

	}

	/**
	 * Returns a copy of the vector with another vector subtracted from it.
	 * Override for 2D vector addition, the z axis of the specified vector is 
	 * implicitly 0.0.
	 * @param  v the vector to subtract
	 * @return   the copy with the new vector subtracted from it
	 */
	public PVec3 sub(PVec2 v) {
		return new PVec3(x - v.x(), y - v.y(), z);

	}

	/**
	 * Returns a copy of the vector multiplied by a value.
	 * @param  t the value to multiply by
	 * @return   the copy with the value multiplied in
	 */
	public PVec3 mul(double t) {
		return new PVec3(x * t, y * t, z * t);

	}

	/**
	 * Returns a copy of the vector divided by a value.
	 * @param  t the value to divide by
	 * @return   the copy with the value divided out
	 */
	public PVec3 div(double t) {
		return mul(1 / t);

	}

	/**
	 * Returns a negated copy of the vector.
	 * @return the negated copy
	 */
	public PVec3 neg() {
		return new PVec3(-x, -y, -z);

	}

	/**
	 * Returns the squared length of the vector.
	 * @return the squared length of the vector
	 */
	public double lengthSquared() {
		return (x * x) + (y * y) + (z * z);

	}

	/**
	 * Returns the actual length of the vector.
	 * @return the actual length of the vector
	 */
	public double length() {
		return Math.sqrt(lengthSquared());

	}

	/**
	 * Returns a copy normalized to a length of 1.
	 * @return the normalized copy
	 */
	public PVec3 unitVector() {
		return div(length());

	}

	/**
	 * Checks if all of the vector's axes are close to 0.
	 * @return {@code true} if all axes are shorter than 1E-8, else {@code false}
	 */
	public boolean nearZero() {
		double min = 1E-8;
		return (Math.abs(this.x) < min)	&& (Math.abs(this.y) < min) && (Math.abs(this.z) < min);

	}

	/**
	 * Returns a new vector where each axis is the minimum value of the specified 
	 * vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   the new vector with the minimum values in each axis
	 */
	public static PVec3 min(PVec3 u, PVec3 v) {
		PVec3 out = new PVec3();
		for (int i = 0; i < 3; i++) {
			out.setAxis(i, Math.min(u.getAxis(i), v.getAxis(i)));
		}
		return out;

	}

	/**
	 * Returns a new vector where each axis is the maximum value of the specified 
	 * vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   the new vector with the maximum values in each axis
	 */
	public static PVec3 max(PVec3 u, PVec3 v) {
		PVec3 out = new PVec3();
		for (int i = 0; i < 3; i++) {
			out.setAxis(i, Math.max(u.getAxis(i), v.getAxis(i)));
		}
		return out;

	}

	/**
	 * Returns the 3 dimensional dot product of 2 vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   3 dimensional dot product of the specified vectors
	 */
	public static double dot(PVec3 u, PVec3 v) {
		return (u.x() * v.x()) + (u.y() * v.y()) + (u.z() * v.z()); 

	}

	/**
	 * Returns the 3 dimensional cross product of 2 vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   3 dimensional cross product of the specified vectors
	 */
	public static PVec3 cross(PVec3 u, PVec3 v) {
		return new PVec3(u.y() * v.z() - u.z() * v.y(),
				u.z() * v.x() - u.x() * v.z(),
				u.x() * v.y() - u.y() * v.x());

	}

	/**
	 * Returns a string representation of the vector for debugging.
	 * @return the string representation of the vector
	 */
	@Override
	public String toString() {
		return ("x: " + x + "\n"
				+ "y: " + y + "\n"
				+ "z: " + z + "\n");

	}
}
