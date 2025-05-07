package com.ang.peEditor;

import javax.swing.JFrame;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.inputs.PMovementInputListener;
import com.ang.peLib.utils.PConversions;
import com.ang.peLib.graphics.*;

public class PGUIRenderer extends PRenderer {
	public PGUIRenderer(int width, int height, PMouseInputListener mouseListener) {
		super(width, height, mouseListener);
	}

	public PGUIRenderer(int width, int height, PMovementInputListener movementListener) {
		super(width, height, movementListener);
	}

	public JFrame getFrame() {
		return frame;

	}

	public void writeTileAround(PColour colour, int width, int height, int x, int y) {
		int tileColour = processToInt(colour);
		for (int j = y - (height / 2); j < y + (height / 2); j++) {
			for (int i = x - (width / 2); i < x + (width / 2); i++) {
				if (!inBounds(i, j)) {
					continue;

				}
				img.setRGB(i, j, tileColour);
			}
		}
	}

	public void writeMapData(PPMapData mapData, PEditorParams params, PVec2 origin) {
		PColour backgroundColour = params.backgroundColour;
		PColour gridColour = params.gridColour;
		PColour lineColour = params.lineColour;
		PColour cornerColour = params.cornerColour;
		int cornerSize = params.CORNER_SIZE;
		double scale = params.scale;
		writeTile(backgroundColour, width, height, 0, 0);	
		if (scale > 6.0) {
			writeBackgroundGrid(gridColour, params, origin);
		}
		for (PSector sec : mapData.world.getSectors()) {
			PVec2[] corners = sec.getCorners();
			for (int i = 0; i < corners.length; i++) {
				int next;
				if (i < corners.length - 1) {
					next = i + 1;
				} else {
					next = 0;
				}
				int[] coords = PConversions.v2ss(corners[i], corners[next], 
						width, height, scale, origin);
				int dotRate = Integer.MAX_VALUE;
				if (sec.isPortal(i, next)) {
					dotRate = 10;
				}
				writeLine(lineColour, coords[0], coords[1], coords[2], coords[3], dotRate);
			}
			for (int i = 0; i < corners.length; i++) {
				int[] coords = PConversions.v2ss(corners[i], 
						width, height, scale, origin);
				writeTileAround(cornerColour, cornerSize, cornerSize, 
						coords[0], coords[1]);
			}
		}
	}

	public void writeBackgroundGrid(PColour colour, PEditorParams params, PVec2 origin) {
		double[] a = PConversions.ss2v(-width / 4, 0, width, height, params.scale, origin);
		PVec2 startingPoint = new PVec2(Math.floor(a[0]), Math.floor(a[1]));
		for (int i=0; ; i++) {
			int[] coords = PConversions.v2ss(startingPoint.add(new PVec2(0.0, 1.0 * i)), width, 
					height, params.scale, origin);
			if (coords[1] > height) {
				break;

			}
			writeLine(colour, 0, coords[1], width, coords[1], Integer.MAX_VALUE);
		}
		for (int i=0; ; i++) {
			int[] coords = PConversions.v2ss(startingPoint.add(new PVec2(1.0 * i, 0.0)), width, 
					height, params.scale, origin);
			if (coords[0] > width) {
				break;

			}
			writeLine(colour, coords[0], 0, coords[0], height, Integer.MAX_VALUE);
		}
	}

	public void writeLine(PColour colour, int x0, int y0, int x1, int y1, int dotRate) {
		int lineColour = processToInt(colour);
		if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
			if (x0 > x1) {
				writeLineLow(lineColour, x1, y1, x0, y0, dotRate);
			} else {
				writeLineLow(lineColour, x0, y0, x1, y1, dotRate);
			}
		} else {
			if (y0 > y1) {
				writeLineHigh(lineColour, x1, y1, x0, y0, dotRate);
			} else {
				writeLineHigh(lineColour, x0, y0, x1, y1, dotRate);
			}
		}
	}

	private void writeLineLow(int colour, int x0, int y0, int x1, int y1, int dotRate) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int yIncrement = 1;
		if (dy < 0) {
			yIncrement = -1;
			dy = -dy;
		}
		int error = (2 * dy) - dx;
		int y = y0;
		boolean drawing = true;
		int counter = 0;
		for (int x = x0; x < x1; x++) {
			if (drawing && inBounds(x, y)) {
				img.setRGB(x, y, colour);
			}
			if (error > 0) {
				y += yIncrement;
				error += 2 * (dy - dx);
			} else {
				error += 2 * dy;
			}
			if (counter >= dotRate) {
				drawing = !drawing;
				counter = 0;
			}
			counter++;
		}
	}

	private void writeLineHigh(int colour, int x0, int y0, int x1, int y1, int dotRate) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int xIncrement = 1;
		if (dx < 0) {
			xIncrement = -1;
			dx = -dx;
		}
		int error = (2 * dx) - dy;
		int x = x0;
		boolean drawing = true;
		int counter = 0;
		for (int y = y0; y < y1; y++) {
			if (drawing && inBounds(x, y)) {
				img.setRGB(x, y, colour);
			}
			if (error > 0) {
				x += xIncrement;
				error += 2 * (dx - dy);
			} else {
				error += 2 * dx;
			}
			if (counter >= dotRate) {
				drawing = !drawing;
				counter = 0;
			}
			counter++;
		}
	}
}
