package com.ang.peLib.resources;

import java.io.File;
import java.nio.file.Path;

/**
 * Stores information about resource files that are being loaded.
 */
public class PResource {
	private PResourceType resourceType;
	private String path;
	private File file;

	/**
	 * Constructs a new resource with a type and a filename.
	 * @param resourceType type of resource represented by this instance
	 * @param path 		   the path from the resources directory to the resource
	 * @see       		   PResourceType
	 */
	public PResource(PResourceType resourceType, String path) {
		this.resourceType = resourceType;
		this.path = path;
		this.file = (path == null) ? null : Path.of(path).toFile();
	}

	/**
	 * Returns the type of the resource.
	 * @return the resource's type
	 * @see    PResourceType
	 */
	public PResourceType getResourceType() {
		return resourceType;

	}
	
	/**
	 * Returns the path to the resource starting from the resources directory.
	 * @return the path to the resource
	 */
	public String getPath() {
		return path;

	}

	/**
	 * Returns the path to the resource as a {@link java.nio.file.Path} object.
	 * @return the path object
	 * @see    java.nio.file.Path
	 */
	public Path getPathObject() {
		return Path.of(path);

	}

	/**
	 * Sets the path to the resource.
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Checks if the resource file exists.
	 * @return {@code true} if the file exists, else {@code false}
	 */
	public boolean exists() {
		return (this.getClass().getResource(path) != null);

	}

	/**
	 * Checks if the resource file matches expected naming conventions and
	 * directory structures for the given resource type.
	 * @return {@code true} if the resource is valid, else {@code false}
	 */
	public boolean valid() {
		switch (resourceType) {
		case PMAP:
			if ((file.getParentFile().getName() == null)) {
				return false;

			}
			if (!path.endsWith(".pmap")) {
				return false;

			}
			if (!file.getParentFile().getName().equals("map")) {
				return false;

			}
			return true;

		case SPRITE:
			// TODO: implement
			return true;

		default:
			return false;

		}
	}

	/**
	 * Returns a new invalid resource.
	 * The resource is invalid as its resource type is invalid and its path is null
	 * @return the invalid resource
	 */
	public static PResource invalid() {
		return new PResource(PResourceType.INVALID, null);
	}

	/**
	 * Returns a string representation of the resource for debugging.
	 * @return the string representation of the resource
	 */
	@Override
	public String toString() {
		return "path: " + path + " type " + resourceType.toString();

	}
}
