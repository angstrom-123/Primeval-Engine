package com.ang.peCore;

import com.ang.peCore.threads.PUpdateWorker;

import java.util.stream.IntStream;

public class PGlobal {
	public static PUpdateWorker uw;
	public final static boolean isWindows = System.getProperty("os.name").
			startsWith("Windows");
	public static String toWindowsPath(String path) {
		String out = "";
		path = path.trim();
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '\\') {
				out += "/";
			} else {
				out += path.charAt(i);
			}
		}
		return out;

	}

	public static Integer[] toIntegerArray(int[] array) {
		return IntStream.of(array).boxed().toArray(Integer[]::new);
	}
}
