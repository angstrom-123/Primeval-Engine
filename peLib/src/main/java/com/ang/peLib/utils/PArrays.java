package com.ang.peLib.utils;

import java.lang.reflect.Array;

public class PArrays {
	@SuppressWarnings("unchecked")
	public static <T extends PCopyable> T[] copy(T[] array, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, array.length);
		for (int i = 0; i < array.length; i++) {
			out[i] = type.cast(array[i]).copy();
		}
		return out;

	}

	@SuppressWarnings("unchecked")
	public static <T> T[] reduceArray(T[] array, int length, Class<T> type) {
		T[] out = (T[]) Array.newInstance(type, length);
		for (int i = 0; i < length; i++) {
			out[i] = (T) array[i];
		}
		return out;

	}

	// separate as primitives cannot be generic
	public static int[] reduceArray(int[] array, int length) {
		int[] out = new int[length];
		for (int i = 0; i < length; i++) {
			out[i] = array[i];
		}
		return out;

	}
}
