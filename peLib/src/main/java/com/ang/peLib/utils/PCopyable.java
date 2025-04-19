package com.ang.peLib.utils;

/**
 * Provides a base class for copyable objects for filtering generics.
 * Generic methods in {@link com.ang.peLib.utils.PArrays} use this to filter 
 * input parameters.
 * @see PArrays
 */
public abstract class PCopyable {
	/**
	 * Returns a deep copy of the object.
	 * @return the deep copy of the object
	 */
	public abstract <T> T copy();
}
