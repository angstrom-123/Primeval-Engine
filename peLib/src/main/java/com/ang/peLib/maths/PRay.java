package com.ang.peLib.maths;

public class PRay {
	private PVec2 origin;
	private PVec2 direction;

	public PRay(PVec2 origin, PVec2 direction) {
		this.origin = origin;
		this.direction = direction;
	}

	public PVec2 getOrigin() {
		return origin;

	}

	public PVec2 getDirection() {
		return direction;

	}

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

	public PVec2 at(double t) {
		return origin.add(direction.mul(t));

	}
}
