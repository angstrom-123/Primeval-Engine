package com.ang.peLib.resources;

/**
 * Holds different types of resource and their corresponding names.
 */
public enum PResourceType {
	SPRITE("sprite"),
	PMAP("pmap"),
	INVALID("invalid");

	private String typeString;

	/**
	 * Private constructor for setting the name of the resource.
	 * @param typeString the string representation of the resource type
	 */
	private PResourceType(String typeString) {
		this.typeString = typeString;
	}

	/**
	 * Returns a string representation of the resource for debugging.
	 * @return the string representation of the resource type
	 */
	@Override
	public String toString() {
		return typeString;

	}
}
