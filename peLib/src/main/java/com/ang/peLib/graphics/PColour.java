package com.ang.peLib.graphics;

public class PColour {
	double r;
	double g;
	double b;

	public PColour() {}

	public PColour(double r, double g, double b){
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public double r() {
		return r;

	}

	public double g() {
		return g;

	}
	
	public double b() {
		return b;

	}

	public PColour mul(double t) {
		return new PColour(r * t, g * t, b * t);

	}

	public double component(int component) {
		switch (component) {
		case 0:
			return r;

		case 1:
			return g;

		case 2:
			return b;

		default:
			return 0.0;

		}
	}

	public void setComponent(int component, double val) {
		switch (component) {
		case 0:
			r = val;
			break;

		case 1:
			g = val;
			break;

		case 2:
			b = val;
			break;

		default:
			break;

		}
	}

	@Override
	public String toString() {
		return ("r: " + r + "\n"
				+ "g: " + g + "\n"
				+ "b: " + b);

	}
}
