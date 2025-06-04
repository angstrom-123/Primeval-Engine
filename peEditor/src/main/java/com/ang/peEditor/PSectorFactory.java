package com.ang.peEditor;

import com.ang.peLib.hittables.PSector;
import com.ang.peLib.maths.PVec2;

/**
 * Helps with constructing a new sector.
 */
public class PSectorFactory {
	private final static int MINIMUM_CORNERS = 3;

	/**
	 * Returns a new sector with a given corner count, scale, and position.
	 * Generated sectors are perfectly circular with radius = scale. The points 
	 * are spread evenly around the circle
	 * @param  cornerCount amount of corners for the new sector
	 * @param  scale 	   scale of the new sector
	 * @param  at		   world space position of the new sector
	 * @return 			   the new sector
	 */
	public static PSector newSector(int cornerCount, double scale, PVec2 at) {
		if (cornerCount < MINIMUM_CORNERS) {
			return null;

		}
		PVec2[] corners = new PVec2[cornerCount];
		corners[0] = new PVec2(0.0, 0.0);
		for (int i = 1; i < cornerCount; i++) {
			corners[i] = new PVec2(
				corners[0].x() + scale * Math.cos((i * 2.0 * Math.PI) / (double) cornerCount),
				corners[0].y() + scale * Math.sin((i * 2.0 * Math.PI) / (double) cornerCount)
			);
		}
		corners[0] = new PVec2(scale, 0.0);
		for (int i = 0; i < corners.length; i++) {
			corners[i] = corners[i].add(at);
		}
		return new PSector(corners, new int[0]);

	}
}
