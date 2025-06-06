package com.ang.peLib.debug;

import com.ang.peLib.graphics.PColour;
import com.ang.peLib.graphics.PRenderer;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.inputs.PMouseInputInterface;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.utils.PConversions;

/**
 * Provides simple visualization of worlds, sectors, and lines for debugging.
 */
public class PVisualizer implements PMouseInputInterface {
	private final int WIDTH = 1200;
	private final int HEIGHT = (int) Math.round((double) WIDTH / (16.0 / 9.0));
	// private final double SCALE = 20.0;
	private final double SCALE = 40.0;
	// private final PVec2 offset = new PVec2(0.0, 10.0);
	private final PVec2 offset = new PVec2(0.0, 0.0);
	private PRenderer renderer;

	/**
	 * Constructs a new visualizer.
	 */
	public PVisualizer() {
		PMouseInputListener listener = new PMouseInputListener(this);
		renderer = new PRenderer(WIDTH, HEIGHT, listener);
		renderer.init();
		renderer.setScale(1.0);
	}

	/**
	 * Renders a sector world to the screen with random colour for each sector and 
	 * index labels for each corner.
	 * @param world the sector world to render 
	 * @see   com.ang.peLib.hittables.PSectorWorld
	 */
	public void visualize(PSectorWorld world) {
		renderer.fillTile(PColour.BLACK, WIDTH, HEIGHT, 0, 0);
		for (PSector sec : world.getSectors()) visualize(sec, false);
		renderer.repaint();
	}

	/**
	 * Renders a sector to the screen with random colour and index labels for each corner.
	 * @param sec 	  the sector to render 
	 * @param doClear if the screen should be wiped before rendering the sector
	 * @see   com.ang.peLib.hittables.PSector
	 */
	public void visualize(PSector sec, boolean doClear) {
		if (doClear) renderer.fillTile(PColour.BLACK, WIDTH, HEIGHT, 0, 0);
		PColour colour = new PColour(Math.random(), Math.random(), Math.random());
		for (int i = 0; i < sec.getCorners().length; i++) {
			int nextI = (i == sec.getCorners().length - 1) ? 0 : i + 1;
			int[] coords = PConversions.v2ss(sec.getCorner(i), sec.getCorner(nextI), 
					WIDTH, HEIGHT, SCALE, offset);
			if (!sec.isPortal(i, nextI)) {
				renderer.writeLine(colour, coords[0], coords[1], coords[2], coords[3]);
			} else {
				renderer.writeLine(colour, coords[0], coords[1], coords[2], coords[3], 5);
			}
			renderer.writeText(String.valueOf(i), coords[0], coords[1]);
		}
		renderer.repaint();
	}

	/**
	 * Renders a line between 2 points.
	 * @param colour the colour of the line to render 
	 * @param from   the starting point of the line 
	 * @param to 	 the ending point of the line
	 */
	public void showLine(PColour colour, PVec2 from, PVec2 to) {
		int[] coords = PConversions.v2ss(from, to, WIDTH, HEIGHT, SCALE, offset);
		renderer.writeLine(colour, coords[0], coords[1], coords[2], coords[3]);
		renderer.repaint();
	}

	// Overriding mouse listener functions for future use

	@Override
	public void mouseScrolled(int x, int y, int units) {}

	@Override
	public void mouseMoved(int x, int y) {}

	@Override
	public void mouseDragged(int x, int y) {}

	@Override
	public void mousePressed(int x, int y) {}

	@Override
	public void rightMousePressed(int x, int y) {}

	@Override
	public void mouseReleased(int x, int y) {}

	@Override
	public void rightMouseReleased(int x, int y) {}

	@Override
	public void mouseExited() {}
}
