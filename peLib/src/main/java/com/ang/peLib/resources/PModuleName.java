package com.ang.peLib.resources;

public enum PModuleName {
	PARENT("primevalEngine"),
	CORE("peCore"),
	EDITOR("peEditor"),
	LIB("peLib");

	private String dirName;

	private PModuleName(String dirName) {
		this.dirName = dirName;
	}

	public String getDirName() {
		return dirName;

	}
}
