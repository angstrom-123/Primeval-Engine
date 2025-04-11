package com.ang.peLib.resources;

import java.io.File;
import java.nio.file.Path;

public class PResource {
	private PResourceType resourceType;
	private String path;
	private File file;

	public PResource(PResourceType resourceType, String path) {
		this.resourceType = resourceType;
		this.path = path;
		this.file = (path == null) ? null : Path.of(path).toFile();
	}

	public PResourceType getResourceType() {
		return resourceType;

	}
	
	public String getPath() {
		return path;

	}

	public Path getPathObject() {
		return Path.of(path);

	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean exists() {
		return (this.getClass().getResource(path) != null);

	}

	public boolean valid() {
		switch (resourceType) {
		case PMAP:
			if ((file.getParentFile().getName() == null) || !path.endsWith(".pmap") 
					|| (!file.getParentFile().getName().equals("mapData"))) {
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

	@Override
	public String toString() {
		return "path: " + path + " type " + resourceType.toString();

	}

	public static PResource invalid() {
		return new PResource(PResourceType.INVALID, null);
	}
}
