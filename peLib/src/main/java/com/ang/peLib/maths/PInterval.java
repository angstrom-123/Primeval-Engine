package com.ang.peLib.maths;

public class PInterval {
	double min;
	double max;

	public PInterval(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public PInterval copy() {
		return new PInterval(min, max);

	}

	public boolean contains(double val) {
		if (min < max) {
			if ((val >= min) && (val <= max)) {
				return true;

			}
		} else if (max < min) {
			if ((val <= min) && (val >= max)) {
				return true;

			}
		}
		return false;

	}

	public double min() {
		return min;

	}

	public double max() {
		return max;

	}

	public void setMin(double t) {
		min = t;
	}

	public void setMax(double t) {
		max = t;
	}

	public static PInterval empty() {
		return new PInterval(Double.MAX_VALUE, -Double.MAX_VALUE);

	}

	public static PInterval universe() {
		return new PInterval(-Double.MAX_VALUE, Double.MAX_VALUE);

	}

	@Override
	public String toString() {
		return ("min:" + min + "\n" + "max:" + max);

	}
}
