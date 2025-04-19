package com.ang.peLib.maths;

/**
 * Reresents a ray cast in 2D space, with an origin and a direction.
 * @see PVec2
 */
public class PRay {
	private PVec2 origin;
	private PVec2 direction;

	/**
	 * Constructs a new ray with origin and direction.
	 * @param origin    the starting point of the ray
	 * @param direction the direction of the ray
	 * @see				PVec2
	 */
	public PRay(PVec2 origin, PVec2 direction) {
		this.origin = origin;
		this.direction = direction;
	}

	/**
	 * Returns the origin point of the ray.
	 * @return the origin point of the ray
	 * @see    PVec2
	 */
	public PVec2 getOrigin() {
		return origin;

	}

	/**
	 * Returns the direction of the ray.
	 * @return the direction of the ray
	 * @see    PVec2
	 */
	public PVec2 getDirection() {
		return direction;

	}

	/**
	 * Returns a given axis of the ray's direction.
	 * @param  axis the index of the axis to return (0:x, 1:y)
	 * @return 		the value of the direction in the given axis, default to 0.0
	 */
	public double getAxis(int axis) {
		switch (axis) {
		case 0:
			return direction.x();

		case 1:
			return direction.y();

		default:
			return 0.0;

		}
	}

	/**
	 * Returns the coordinate at a specified point along the ray.
	 * @param  t the distance along the ray
	 * @return the coordinate at the specified point along the ray
	 */
	public PVec2 at(double t) {
		return origin.add(direction.mul(t));

	}
}
