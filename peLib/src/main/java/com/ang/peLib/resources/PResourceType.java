package com.ang.peLib.resources;

/**
 * Holds different types of resource and their corresponding names.
 */
public enum PResourceType {
	SPRITE("sprite", "sprite"),
	PMAP("pmap", "map"),
	INVALID("invalid", null);

	private String typeString;
	private String dirName;

	/**
	 * Private constructor for setting the name of the resource.
	 * @param typeString the string representation of the resource type
	 * @param dirName 	 the name of the directory containing this resource type
	 */
	private PResourceType(String typeString, String dirName) {
		this.typeString = typeString;
		this.dirName = dirName;
	}
	
	public String getDirName() {
		return dirName;

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
