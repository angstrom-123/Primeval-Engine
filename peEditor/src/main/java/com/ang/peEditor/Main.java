package com.ang.peEditor;

public class Main {
    public static void main(String[] args) {
		PEditor e = new PEditor();
		e.start(isTest(args));
    }

	private static boolean isTest(String[] args) {
		final String[] allowed = new String[]{"-t", "--test"};
		if (args.length != 1) {
			return false;

		}
		for (String s : allowed) {
			if (args[0].equals(s)) {
				return true;

			}
		}
		return false;

	}
}
