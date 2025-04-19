package com.ang.peCore;

import com.ang.peCore.threads.PUpdateWorker;

import java.util.stream.IntStream;

public class PGlobal {
	public static PUpdateWorker uw;
	public static Integer[] toIntegerArray(int[] array) {
		return IntStream.of(array).boxed().toArray(Integer[]::new);
	}
}
