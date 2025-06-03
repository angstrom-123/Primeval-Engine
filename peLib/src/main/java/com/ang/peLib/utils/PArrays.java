package com.ang.peLib.utils;

import java.lang.reflect.Array;

/**
 * Provides utility functions for manipulating arrays.
 */
public class PArrays {
	/**
	 * Returns a deep copy of the input array.
	 * No bounds checks are performed on the array or the length, out of bounds
	 * errors should be handled before calling this function.
	 * @param  array an object array to be copied
	 * @param  type  the datatype of the array elements
	 * @return 		 a deep copy of the specified array
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PCopyable> T[] copy(T[] array, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, array.length);
		for (int i = 0; i < array.length; i++) out[i] = type.cast(array[i]).copy();
		return out;

	}

	/**
	 * Returns a copy of the input array with empty elements stripped from the end.
	 * No bounds checks are performed on the array or the length, out of bounds
	 * errors should be handled before calling this function.
	 * @param  array  an int array to be reduced
	 * @param  length the length of the array to return
	 * @param  type   the datatype of the array elements
	 * @return 		  the new array of the specified length
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] reduceArray(T[] array, int length, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, length);
		for (int i = 0; i < length; i++) out[i] = (T) array[i];
		return out;

	}

	@SuppressWarnings("unchecked")
	public static <T> T[] combineArrays(T[] array1, T[] array2, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, array1.length + array2.length);
		int head = 0;
		for (int i = 0; i < array1.length; i++) out[head++] = array1[i];
		for (int i = 0; i < array2.length; i++) out[head++] = array2[i];
		return out;

	}

	@SuppressWarnings("unchecked")
	public static <T> T[] reverse(T[] array, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, array.length);
		for (int i = 0; i < array.length; i++) out[i] = array[array.length - 1 - i];
		return out;

	}

	public static int[] reverse(int[] array) {
		int[] out = new int[array.length];
		for (int i = 0; i < array.length; i++) out[i] = array[array.length - 1 - i];
		return out;

	}

	/**
	 * Returns a copy of the input array with empty elements stripped from the end.
	 * No bounds checks are performed on the array or the length, out of bounds
	 * errors should be handled before calling this function.
	 * <p>
	 * This function is separate from the generic one as primitives cannot be
	 * generic (instances such as int and double are not objects).
	 * @param  array  an int array to be reduced
	 * @param  length the length of the array to return
	 * @return 		  the new array of the specified length
	 */
	public static int[] reduceArray(int[] array, int length) {
		int[] out = new int[length];
		for (int i = 0; i < length; i++) out[i] = array[i];
		return out;

	}
}
