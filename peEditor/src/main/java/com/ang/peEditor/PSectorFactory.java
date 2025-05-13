package com.ang.peEditor;

import com.ang.peLib.hittables.PSector;
import com.ang.peLib.maths.PVec2;

public class PSectorFactory {
	private final static int MAXIMUM_CORNERS = 8;
	private final static int MINIMUM_CORNERS = 3;
	private final static double SIDE_LEN = 2.0;

	public static PSector newSector(int cornerCount, PVec2 at) {
		if ((cornerCount < MINIMUM_CORNERS) || (cornerCount > MAXIMUM_CORNERS)) {
			return null;

		}
		PVec2[] corners = new PVec2[cornerCount];
		corners[0] = new PVec2(0.0, 0.0);
		for (int i = 1; i < cornerCount; i++) {
			corners[i] = new PVec2(
				corners[0].x() + SIDE_LEN * Math.cos((i * 2.0 * Math.PI) / (double) cornerCount),
				corners[0].y() + SIDE_LEN * Math.sin((i * 2.0 * Math.PI) / (double) cornerCount)
			);
		}
		corners[0] = new PVec2(SIDE_LEN, 0.0);
		for (int i = 0; i < corners.length; i++) {
			corners[i] = corners[i].add(at);
		}
		return new PSector(corners, new int[0]);

	}
}
