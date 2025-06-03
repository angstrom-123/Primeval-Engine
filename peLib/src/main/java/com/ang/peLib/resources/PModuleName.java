package com.ang.peLib.resources;

/**
 * Specifies the name of modules in the engine.
 */
public enum PModuleName {
	PARENT("primevalEngine"),
	CORE("peCore"),
	EDITOR("peEditor"),
	LIB("peLib");

	private String dirName;

	/**
	 * Constructs the module name with a string representing its directory name 
	 * from the project's root.
	 * @param dirName name of the root directory of this module
	 */
	private PModuleName(String dirName) {
		this.dirName = dirName;
	}

	/**
	 * Returns the directory name of the module.
	 * @return directory name of the module
	 */
	public String getDirName() {
		return dirName;

	}
}
