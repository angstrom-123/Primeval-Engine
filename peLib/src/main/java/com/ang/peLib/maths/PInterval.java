package com.ang.peLib.maths;

import com.ang.peLib.utils.PCopyable;

/**
 * Represents a mathematical interval and provides utilities.
 */
public class PInterval extends PCopyable {
	double min;
	double max;

	/**
	 * Constructs an interval between 2 values.
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public PInterval(double min, double max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PInterval copy() {
		return new PInterval(min, max);

	}

	/**
	 * Checks if a value is within the interval (exclusive of bounds).
	 * @param  val the value to check
	 * @return {@code true} if the value is between the bounds, else {@code false}
	 */
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

	/**
	 * Returns the minimum value of the interval.
	 * @return the minimum value of the interval
	 */
	public double getMin() {
		return min;

	}

	/**
	 * Returns the maximum value of the interval.
	 * @return the maximum value of the interval
	 */
	public double getMax() {
		return max;

	}

	/**
	 * Sets the mimimum value of the interval.
	 * @param t the new value for the minimum
	 */
	public void setMin(double t) {
		min = t;
	}

	/**
	 * Sets the maximum value of the interval.
	 * @param t the new value for the maximum
	 */
	public void setMax(double t) {
		max = t;
	}

	/**
	 * Returns a new interval that cannot contain any values.
	 * @return a new interval that cannot contain any values
	 */
	public static PInterval empty() {
		return new PInterval(Double.MAX_VALUE, -Double.MAX_VALUE);

	}

	/**
	 * Returns a new interval contains all values.
	 * @return a new interval contains all values
	 */
	public static PInterval universe() {
		return new PInterval(-Double.MAX_VALUE, Double.MAX_VALUE);

	}

	/**
	 * Returns a string representation of the interval for debugging.
	 * @return the string representation of the interval
	 */
	@Override
	public String toString() {
		return ("min:" + min + "\n" + "max:" + max);

	}
}
