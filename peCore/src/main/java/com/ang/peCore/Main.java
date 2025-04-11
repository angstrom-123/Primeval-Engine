package com.ang.peCore;

public class Main {
    public static void main(String[] args) {
		PGame g = new PGame();
		g.start(parseArgs(args));
    }

	public static PMode[] parseArgs(String[] args) {
		PMode[] out = new PMode[args.length];
		int head = 0;
		for (String arg : args) {
			switch (arg) {
			case "-test":
			case "-t":
				out[head++] = PMode.TEST;
				break;

			case "-editor":
			case "-edit":
			case "-e":
				out[head++] = PMode.EDIT;
				break;

			case "-game":
			case "-g":
				out[head++] = PMode.GAME;
				break;

			default:
				System.err.println("Invalid arguments");
				return new PMode[0];

			}
		}
		return out;

	}

}
