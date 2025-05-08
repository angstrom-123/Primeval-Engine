package com.ang.peLib.maths;

import com.ang.peLib.utils.PCopyable;

/**
 * Represents a 2 dimensional vector.
 */
public class PVec2 extends PCopyable {
	private double x = 0.0;
	private double y = 0.0;

	/**
	 * Default constructor.
	 */
	public PVec2() {}

	/**
	 * Constructs the vector using an array of doubles.
	 * @param vals a 2-long array of doubles representing x and y respectively
	 */
	public PVec2(double[] vals) {
		if (vals.length != 2) {
			throw new NullPointerException();
		
		}
		this.x = vals[0];
		this.y = vals[1];
	}

	/**
	 * Constructs the vector using 2 doubles.
	 * @param x the x value
	 * @param y the y value
	 */
	public PVec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PVec2 copy() {
		return new PVec2(x, y);

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
	 * Returns a copy converted to a 3 dimensional vector.
	 * The 3rd (z) axis defaults to 0.0
	 * @return the 3D vector
	 * @see    PVec3
	 */
	public PVec3 toVec3() {
		return new PVec3(x, y, 0.0);

	}	

	/**
	 * Sets a given axis.
	 * @param axis the index of the axis to set (0:x, 1:y)
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

		default:
			return 0.0;

		}
	}

	/**
	 * Returns a copy of the vector with another vector added to it.
	 * @param  v the vector to add
	 * @return   the copy with the new vector added to it
	 */
	public PVec2 add(PVec2 v) {
		return new PVec2(this.x + v.x(), this.y + v.y());

	}

	/**
	 * Returns a copy of the vector with another vector subtracted from it.
	 * @param  v the vector to subtract
	 * @return   the copy with the new vector subtracted from it
	 */
	public PVec2 sub(PVec2 v) {
		return new PVec2(this.x - v.x(), this.y - v.y());

	}

	/**
	 * Returns a copy of the vector multiplied by a value.
	 * @param  t the value to multiply by
	 * @return   the copy with the value multiplied in
	 */
	public PVec2 mul(double t) {
		return new PVec2(this.x * t, this.y * t);

	}

	/**
	 * Returns a copy of the vector divided by a value.
	 * @param  t the value to divide by
	 * @return   the copy with the value divided out
	 */
	public PVec2 div(double t) {
		return mul(1 / t);

	}

	/**
	 * Returns a rounded copy of the vector.
	 * @return   the rounded copy
	 */
	public PVec2 round() {
		return new PVec2(Math.round(x), Math.round(y));

	}

	/**
	 * Returns a negated copy of the vector.
	 * @return the negated copy
	 */
	public PVec2 neg() {
		return new PVec2(-x, -y);

	}

	/**
	 * Returns the squared length of the vector.
	 * @return the squared length of the vector
	 */
	public double lengthSquared() {
		return (this.x * this.x) + (this.y * this.y);

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
	public PVec2 unitVector() {
		return div(length());

	}

	/**
	 * Checks if both of the vector's axes are close to 0.
	 * @return {@code true} if both axes are shorter than 1E-8, else {@code false}
	 */
	public boolean nearZero() {
		final double min = 1E-8;
		return (Math.abs(this.x) < min)	&& (Math.abs(this.y) < min);

	}

	/**
	 * Returns the 2 dimensional cross product of 2 vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   2 dimensional cross product of the specified vectors
	 */
	public static double cross(PVec2 u, PVec2 v) {
		return (u.x() * v.y()) - (u.y() * v.x());

	}

	/**
	 * Returns the 2 dimensional dot product of 2 vectors.
	 * @param  u the first vector
	 * @param  v the second vector
	 * @return   2 dimensional dot product of the specified vectors
	 */
	public static double dot(PVec2 u, PVec2 v) {
		return (u.x() * v.x()) + (u.y() * v.y()); 

	}

	/**
	 * Returns a string representation of the vector for debugging.
	 * @return the string representation of the vector
	 */
	@Override
	public String toString() {
		return ("x: " + x + "\n" + "y: " + y);

	}
}
