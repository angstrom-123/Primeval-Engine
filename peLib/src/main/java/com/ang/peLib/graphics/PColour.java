package com.ang.peLib.graphics;

import com.ang.peLib.utils.PCopyable;

/**
 * Represents a colour with 3 channels (r, g, b) and provides utilities.
 */
public class PColour extends PCopyable {
	private double r;
	private double g;
	private double b;

	public final static PColour WHITE = new PColour(1.0, 1.0, 1.0);
	public final static PColour BLACK = new PColour(0.0, 0.0, 0.0);
	public final static PColour RED = new PColour(1.0, 0.0, 0.0);
	public final static PColour GREEN = new PColour(0.0, 1.0, 0.0);
	public final static PColour BLUE = new PColour(0.0, 0.0, 1.0);

	/**
	 * Default constructor.
	 */
	public PColour() {}

	/**
	 * Constructor that sets each colour channel.
	 * @param r the intensity (0.0 - 1.0) of the red channel
	 * @param g the intensity (0.0 - 1.0) of the green channel
	 * @param b the intensity (0.0 - 1.0) of the blue channel
	 */
	public PColour(double r, double g, double b){
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public PColour(double[] vals) {
		if (vals.length != 3) {
			throw new IndexOutOfBoundsException();

		}
		this.r = vals[0];
		this.g = vals[1];
		this.b = vals[2];
	}

	/**
	 * Returns the intensity of the red channel.
	 * @return the red channel
	 */
	public double r() {
		return r;

	}

	/**
	 * Returns the intensity of the green channel.
	 * @return the green channel
	 */
	public double g() {
		return g;

	}
	
	/**
	 * Returns the intensity of the blue channel.
	 * @return the blue channel
	 */
	public double b() {
		return b;

	}

	/**
	 * Returns a copy of the colour that has each channel multiplied by a value.
	 * @param  t the value to multiply in
	 * @return   the copy with the value multiplied in
	 */
	public PColour mul(double t) {
		return new PColour(r * t, g * t, b * t);

	}

	/**
	 * Multiplies each component in this colour by another.
	 * @param c the colour to multiply by
	 */
	public PColour mul(PColour c) {
		return new PColour(this.r * c.r, this.g * c.g, this.b * c.b);
	}

	/**
	 * Returns a given colour channel.
	 * @param  component the index of the component to return (0:r, 1:g, 2:b)
	 * @return 			 intensity of the given component
	 */
	public double getComponent(int component) {
		switch (component) {
		case 0 -> { return r; }
		case 1 -> { return g; }
		case 2 -> { return b; }
		default -> throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Sets a given colour channel.
	 * @param component the index of the component to set (0:r, 1:g, 2:b)
	 * @param val  		the value to assign to the component
	 */
	public void setComponent(int component, double val) {
		switch (component) {
		case 0 -> r = val;
		case 1 -> g = val;
		case 2 -> b = val;
		default -> throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PColour copy() {
		return new PColour(r, g, b);

	}

	/**
	 * Returns a string representation of the colour for debugging.
	 * @return the string representation of the colour
	 */
	@Override
	public String toString() {
		return ("r: " + r + "\n"
				+ "g: " + g + "\n"
				+ "b: " + b);

	}
}
