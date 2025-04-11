package com.ang.peLib.resources;

public enum PResourceType {
	SPRITE("sprite"),
	PMAP("pmap"),
	INVALID("invalid");

	private String typeString;

	private PResourceType(String typeString) {
		this.typeString = typeString;
	}

	@Override
	public String toString() {
		return typeString;

	}
}
