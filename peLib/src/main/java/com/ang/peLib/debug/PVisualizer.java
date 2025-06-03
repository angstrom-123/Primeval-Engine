package com.ang.peLib.debug;

import com.ang.peLib.graphics.PColour;
import com.ang.peLib.graphics.PRenderer;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.inputs.PMouseInputInterface;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.utils.PConversions;

public class PVisualizer implements PMouseInputInterface {
	private final int WIDTH = 1200;
	private final int HEIGHT = (int) Math.round((double) WIDTH / (16.0 / 9.0));
	// private final double SCALE = 20.0;
	private final double SCALE = 40.0;
	// private final PVec2 offset = new PVec2(0.0, 10.0);
	private final PVec2 offset = new PVec2(0.0, 0.0);
	private PRenderer renderer;

	public PVisualizer() {
		PMouseInputListener listener = new PMouseInputListener(this);
		renderer = new PRenderer(WIDTH, HEIGHT, listener);
		renderer.init();
		renderer.setScale(1.0);
	}

	public void visualize(PSectorWorld world) {
		renderer.fillTile(PColour.BLACK, WIDTH, HEIGHT, 0, 0);
		for (PSector sec : world.getSectors()) visualize(sec, false);
		renderer.repaint();
	}

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

	public void showLine(PColour colour, PVec2 from, PVec2 to) {
		int[] coords = PConversions.v2ss(from, to, WIDTH, HEIGHT, SCALE, offset);
		renderer.writeLine(colour, coords[0], coords[1], coords[2], coords[3]);
		renderer.repaint();
	}

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
