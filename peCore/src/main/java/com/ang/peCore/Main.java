package com.ang.peCore;

public class Main {
    public static void main(String[] args) {
		PGame g = new PGame();
		g.start(isTest(args));
    }

	public static boolean isTest(String[] args) {
		final String[] allowed = new String[]{"--test", "-t"};
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
