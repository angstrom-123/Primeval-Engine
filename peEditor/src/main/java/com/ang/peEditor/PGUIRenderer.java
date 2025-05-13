package com.ang.peEditor;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.ang.peLib.maths.PVec2;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.utils.PConversions;
import com.ang.peLib.graphics.*;

public class PGUIRenderer extends PRenderer {
	private JPanel subPanel;
	private JLayeredPane layerPane = new JLayeredPane();	

	public PGUIRenderer(int width, int height, PMouseInputListener mouseListener) {
		super(width, height, mouseListener);
		this.initGui();
	}

	private void initGui() {
		frame.setPreferredSize(new Dimension(width, height));
		layerPane.setBounds(0, 0, width, height);
		layerPane.setPreferredSize(new Dimension(width, height));
		layerPane.setLayout(null);
		imgPanel.setBounds(0, 0, width, height);
		imgPanel.setPreferredSize(new Dimension(width, height));
		imgPanel.setFocusable(true);
		imgPanel.requestFocusInWindow();
		imgPanel.setOpaque(true);
		PMouseInputListener mil = (PMouseInputListener) listener;
		imgPanel.addMouseMotionListener(mil);
		imgPanel.addMouseListener(mil);
		imgPanel.addMouseWheelListener(mil);
		frame.getContentPane().add(layerPane);
		layerPane.add(imgPanel, Integer.valueOf(1));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
	}

	public JFrame getFrame() {
		return frame;

	}

	public JPanel getSubPanel() {
		return subPanel;

	}

	public void addSubPanel(JPanel panel) {
		if (subPanel != null) {
			removeSubPanel();
		}
		panel.setOpaque(true);
		panel.setBounds(width - panel.getWidth(), 0, panel.getWidth(), panel.getHeight());
		subPanel = panel;
		layerPane.add(panel, Integer.valueOf(2));
	}

	public void removeSubPanel() {
		if (subPanel != null) {
			layerPane.remove(subPanel);
			subPanel = null;
		}
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

	public void writeTileAroundCorner(PPMapData mapData, PEditorParams params, PVec2 origin,
			PColour colour, int sectorIndex, int cornerIndex) {
		PVec2 corner = mapData.world.getSector(sectorIndex).getCorner(cornerIndex);
		int[] coords = PConversions.v2ss(corner, params.width, params.height, 
				params.scale, origin);
		writeTileAround(colour, params.CORNER_SIZE, params.CORNER_SIZE, coords[0], coords[1]);
	}

	public void writeMouseCoords(int x, int y) {
		String xString = String.valueOf(x);
		String yString = String.valueOf(y);
		writeTile(PColour.BLACK, 63, 14, 0, 0);
		img.getGraphics().drawString("x:" + xString + " y:" + yString, 0, 10);
	}

	public void writeMapData(PPMapData mapData, PEditorParams params, PVec2 origin) {
		writeTile(params.backgroundColour, width, height, 0, 0);	
		if (params.scale > 6.0) {
			writeBackgroundGrid(params.gridColour, params, origin);
		}
		writeXYAxisLines(params.axisColour, params, origin);
		for (PSector sec : mapData.world.getSectors()) {
			PVec2[] corners = sec.getCorners();
			for (int i = 0; i < corners.length; i++) {
				int next = (i < corners.length - 1) ? i + 1 : 0;
				int[] coords = PConversions.v2ss(corners[i], corners[next], 
						width, height, params.scale, origin);
				int dotRate = (sec.isPortal(i, next)) ? 10 : Integer.MAX_VALUE;
				writeLine(params.lineColour, coords[0], coords[1], coords[2], 
						coords[3], dotRate);
			}
			for (int i = 0; i < corners.length; i++) {
				int[] coords = PConversions.v2ss(corners[i], width, height, 
						params.scale, origin);
				writeTileAround(params.cornerColour, params.CORNER_SIZE, 
						params.CORNER_SIZE, coords[0], coords[1]);
			}
		}
	}

	public void writeBackgroundGrid(PColour colour, PEditorParams params, PVec2 origin) {
		double[] startCoords = PConversions.ss2v(-width / 4, 0, width, 
				height, params.scale, origin);
		PVec2 startingPoint = new PVec2(Math.floor(startCoords[0]), Math.floor(startCoords[1]));
		for (int i = 0; ; i++) {
			PVec2 linePoint = startingPoint.add(new PVec2(0.0, 1.0 * i));
			int[] coords = PConversions.v2ss(linePoint, width, height, params.scale, origin);
			if (coords[1] > height) break;

			writeLine(colour, 0, coords[1], width, coords[1], Integer.MAX_VALUE);
		}
		for (int i = 0; ; i++) {
			PVec2 linePoint = startingPoint.add(new PVec2(1.0 * i, 0.0));
			int[] coords = PConversions.v2ss(linePoint, width, height, params.scale, origin);
			if (coords[0] > width) break;

			writeLine(colour, coords[0], 0, coords[0], height, Integer.MAX_VALUE);
		}
	}

	public void writeXYAxisLines(PColour colour, PEditorParams params, PVec2 origin) {
		PVec2 zeroZero = new PVec2(0.0, 0.0);
		int[] coords = PConversions.v2ss(zeroZero, width, height, params.scale, origin);
		writeLine(colour, coords[0], 0, coords[0], height, Integer.MAX_VALUE);
		writeLine(colour, 0, coords[1], width, coords[1], Integer.MAX_VALUE);
	}

	public void writeLinesToCorner(PColour colour, PVec2 point, int sectorIndex, int cornerIndex,
			PPMapData mapData, PEditorParams params, PVec2 origin) {
		PVec2[] corners = mapData.world.getSector(sectorIndex).getCorners();
		int prevIndex = (cornerIndex == 0) ? corners.length - 1 : cornerIndex - 1;
		int nextIndex = (cornerIndex == corners.length - 1) ? 0 : cornerIndex + 1;
		int[] prevCornerCoords = PConversions.v2ss(corners[prevIndex], params.width, 
				params.height, params.scale, origin);
		int[] nextCornerCoords = PConversions.v2ss(corners[nextIndex], params.width, 
				params.height, params.scale, origin);
		int[] currentCornerCoords = PConversions.v2ss(point, params.width, 
				params.height, params.scale, origin);
		writeLine(colour, currentCornerCoords[0], currentCornerCoords[1], 
				prevCornerCoords[0], prevCornerCoords[1], Integer.MAX_VALUE);
		writeLine(colour, currentCornerCoords[0], currentCornerCoords[1], 
				nextCornerCoords[0], nextCornerCoords[1], Integer.MAX_VALUE);
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
		int yIncrement = (dy < 0) ? -1 : 1;
		dy *= yIncrement;
		int error = (2 * dy) - dx;
		int y = y0;
		boolean doDraw = true;
		int counter = 0;
		for (int x = x0; x < x1; x++) {
			if (doDraw && inBounds(x, y)) {
				img.setRGB(x, y, colour);
			}
			if (error > 0) {
				y += yIncrement;
				error += 2 * (dy - dx);
			} else {
				error += 2 * dy;
			}
			if (counter >= dotRate) {
				doDraw = !doDraw;
				counter = 0;
			}
			counter++;
		}
	}

	private void writeLineHigh(int colour, int x0, int y0, int x1, int y1, int dotRate) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int xIncrement = (dx < 0) ? -1 : 1;
		dx *= xIncrement;
		int error = (2 * dx) - dy;
		int x = x0;
		boolean doDraw = true;
		int counter = 0;
		for (int y = y0; y < y1; y++) {
			if (doDraw && inBounds(x, y)) {
				img.setRGB(x, y, colour);
			}
			if (error > 0) {
				x += xIncrement;
				error += 2 * (dx - dy);
			} else {
				error += 2 * dx;
			}
			if (counter >= dotRate) {
				doDraw = !doDraw;
				counter = 0;
			}
			counter++;
		}
	}
}
