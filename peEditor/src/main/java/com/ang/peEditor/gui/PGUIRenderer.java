package com.ang.peEditor.gui;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import com.ang.peLib.maths.PVec2;
import com.ang.peEditor.PEditorParams;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.inputs.PFullKeyboardInputListener;
import com.ang.peLib.utils.PConversions;
import com.ang.peLib.graphics.*;

/**
 * Provides additional functionality to the renderer for rendering editor components.
 */
public class PGUIRenderer extends PRenderer {
	public final static int LOCATION_LEFT = 0;
	public final static int LOCATION_RIGHT = 1;
	public final static int LOCATION_CENTRE = 2;
	private List<JPanel> subPanels = new ArrayList<JPanel>();
	private JLayeredPane layerPane = new JLayeredPane();	

	/**
	 * Constructs a new gui renderer.
	 * @param width  		width of the screen to render 
	 * @param height 		height of the screen to render 
	 * @param keyListener   listener for detecting keyboard inputs
	 * @param mouseListener listener for detecting mouse motion
	 * @see       			com.ang.peLib.inputs.PMouseInputListener
	 * @see       			com.ang.peLib.inputs.PFullKeyboardInputListener
	 */
	public PGUIRenderer(int width, int height, PFullKeyboardInputListener keyListener, 
			PMouseInputListener mouseListener) {
		super(width, height, mouseListener);
		this.initGui(keyListener, mouseListener);
	}

	/**
	 * Initializes all components for the editor window.
	 * @param kil keyboard listener for detecting keyboard shortcuts 
	 * @param mil mouse listener for detecting mouse events
	 * @see       com.ang.peLib.inputs.PMouseInputListener
	 * @see       com.ang.peLib.inputs.PFullKeyboardInputListener
	 */
	private void initGui(PFullKeyboardInputListener kil, PMouseInputListener mil) {
		frame.setResizable(false);
		layerPane.setBounds(0, 0, width, height);
		layerPane.setPreferredSize(new Dimension(width, height));
		layerPane.setLayout(null);
		imgPanel.setBounds(0, 0, width, height);
		imgPanel.setPreferredSize(new Dimension(width, height));
		imgPanel.setFocusable(true);
		imgPanel.requestFocusInWindow();
		imgPanel.setOpaque(true);
		imgPanel.addMouseMotionListener(mil);
		imgPanel.addMouseListener(mil);
		imgPanel.addMouseWheelListener(mil);
		imgPanel.addKeyListener(kil);
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

	/**
	 * Returns the renderer's jframe.
	 * @return the jframe that this renderer uses as a base
	 */
	public JFrame getFrame() {
		return frame;

	}

	/**
	 * Returns all subpanels in this renderer.
	 * @return the subpanels that this renderer has (data panel, right click menu)
	 */
	public JPanel[] getSubPanels() {
		JPanel[] out = new JPanel[subPanels.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = subPanels.get(i);
		}
		return out;

	}

	/**
	 * Adds a new subpanel to the renderer window.
	 * @param panel the new panel to add
	 */
	public void addSubPanel(JPanel panel) {
		addSubPanel(panel, LOCATION_CENTRE);
	}

	/**
	 * Adds a new subpanel to the renderer window at a specified location.
	 * @param panel the new panel to add
	 * @param x 	screen space x coordinate to insert subpanel
	 * @param y 	screen space y coordinate to insert subpanel
	 */
	public void addSubPanel(JPanel panel, int x, int y) {
		clearSubPanelsOfType(panel.getClass());
		panel.setOpaque(true);
		panel.setBounds(x, y, panel.getWidth(), panel.getHeight());
		subPanels.add(panel);
		layerPane.add(panel, Integer.valueOf(2));
	}

	/**
	 * Adds a new subpanel to the renderer window at a specified location constant.
	 * @param panel    the new panel to add
	 * @param location location constant defined in this file
	 */
	public void addSubPanel(JPanel panel, int location) {
		clearSubPanelsOfType(panel.getClass());
		panel.setOpaque(true);
		switch (location) {
		case LOCATION_LEFT:
			panel.setBounds(0, 0, panel.getWidth(), panel.getHeight());
			break;

		case LOCATION_RIGHT:
			panel.setBounds(width - panel.getWidth(), 0, panel.getWidth(), panel.getHeight());
			break;

		case LOCATION_CENTRE:
		default:
			panel.setBounds((width - panel.getWidth()) / 2, (height - panel.getHeight()) / 2, 
					panel.getWidth(), panel.getHeight());
			break;

		}
		subPanels.add(panel);
		layerPane.add(panel, Integer.valueOf(2));
	}

	/**
	 * Removes all subpanels of a specified type.
	 * @param type the datatype of the panels to remove.
	 */
	public void clearSubPanelsOfType(Class<?> type) {
		for (int i = 0; ; i++) {
			if (i >= subPanels.size()) return;

			if (subPanels.get(i).getClass() == type) {
				JPanel p = subPanels.remove(i);
				layerPane.remove(p);
				i = 0;
			}
		}
	}

	/**
	 * Removes all subpanels;
	 */
	public void clearSubPanels() {
		for (JPanel panel: subPanels) {
			layerPane.remove(panel);
		}
		subPanels.clear();
	}

	/**
	 * Renders a filled circle around a corner in a sector world.
	 * @param mapData 	  map data containing the sector world with the required corner 
	 * @param params 	  editor params for colours, scale, and screen dimensions
	 * @param translation translation of the map relative to the viewport 
	 * @param colour 	  the colour of the circle 
	 * @param sectorIndex index of the sector containing the corner to fill a circle around
	 * @param cornerIndex index of the corner to fill a circle around
	 * @param radius 	  radius in pixels for the circle to draw
	 * @see 			  com.ang.peLib.hittables.PSector
	 * @see 			  com.ang.peLib.hittables.PSectorWorld
	 * @see 			  com.ang.peLib.maths.PVec2
	 * @see 			  com.ang.peLib.graphics.PColour
	 */
	public void fillCircleAroundCorner(PPMapData mapData, PEditorParams params, PVec2 translation,
			PColour colour, int sectorIndex, int cornerIndex, int radius) {
		PVec2 corner = mapData.world.getSector(sectorIndex).getCorner(cornerIndex);
		int[] coords = PConversions.v2ss(corner, params.width, params.height, 
				params.scale, translation);
		fillCircleAround(colour, radius, coords[0], coords[1]);
	}

	/**
	 * Renders a filled circle around a specified coordinate.
	 * @param colour 	  the colour of the circle 
	 * @param radius 	  radius in pixels for the circle to draw
	 * @param x 		  screen space x coordinate to render the circle around
	 * @param y 		  screen space y coordinate to render the circle around
	 * @see 			  com.ang.peLib.graphics.PColour
	 */
	public void fillCircleAround(PColour colour, int radius, int x, int y) {
		int circleColour = processToInt(colour);
		for (int y0 = -radius; y0 <= radius; y0++) {
			for (int x0 = -radius; x0 <= radius; x0++) {
				if (!inBounds(x0 + x, y0 + y)) {
					continue;

				}
				if (x0 * x0 + y0 * y0 <= radius * radius) {
					img.setRGB(x0 + x, y0 + y, circleColour);
				}
			}
		}
	}

	/**
	 * Renders an outline of a circle around a specified coordinate.
	 * @param colour 	  the colour of the circle 
	 * @param radius 	  radius in pixels for the circle to draw
	 * @param x 		  screen space x coordinate to render the circle around
	 * @param y 		  screen space y coordinate to render the circle around
	 * @see 			  com.ang.peLib.graphics.PColour
	 */
	public void writeCircleAround(PColour colour, int radius, int x, int y) {
		int circleColour = processToInt(colour);
		int x0 = 0;
		int y0 = radius;
		int d = 3 - 2 * radius;
		while (y0 >= x0) {
			if (d > 0) {
				y0--;
				d += 4 * (x0 - y0) + 10;
			} else {
				d += 4 * x0 + 6;
			}
			x0++;
			writeSymmetricPixels(circleColour, x, y, x0, y0);
		}
	}

	/**
	 * Helps with rendering a circle.
	 * @param colour integer representation of the colour of the pixels 
	 * @param x 	 starting x in pixels
	 * @param y 	 starting y in pixels
	 * @param x0 	 next x in pixels
	 * @param y0 	 next y in pixels
	 */
	private void writeSymmetricPixels(int colour, int x, int y, int x0, int y0) {
		if (inBounds(x + x0, y + y0)) img.setRGB(x + x0, y + y0, colour);
		if (inBounds(x - x0, y + y0)) img.setRGB(x - x0, y + y0, colour);
		if (inBounds(x + x0, y - y0)) img.setRGB(x + x0, y - y0, colour);
		if (inBounds(x - x0, y - y0)) img.setRGB(x - x0, y - y0, colour);
		if (inBounds(x + y0, y + x0)) img.setRGB(x + y0, y + x0, colour);
		if (inBounds(x - y0, y + x0)) img.setRGB(x - y0, y + x0, colour);
		if (inBounds(x + y0, y - x0)) img.setRGB(x + y0, y - x0, colour);
		if (inBounds(x - y0, y - x0)) img.setRGB(x - y0, y - x0, colour);
	}

	/**
	 * Renders an outline of a rectangle around a specified coordinate.
	 * @param colour the colour of the circle 
	 * @param width  width of the rectangle to render in pixels
	 * @param height height of the rectangle to render in pixels
	 * @param x 	 screen space x coordinate to render the circle around
	 * @param y 	 screen space y coordinate to render the circle around
	 * @see 		 com.ang.peLib.graphics.PColour
	 */
	public void writeTileAround(PColour colour, int width, int height, int x, int y) {
		int x0 = x - width / 2;
		int y0 = y - height / 2;
		writeLine(colour, x0, y0, x0 + width, y0, Integer.MAX_VALUE);
		writeLine(colour, x0, y0, x0, y0 + height, Integer.MAX_VALUE);
		writeLine(colour, x0 + width, y0 + height, x0, y0 + height, Integer.MAX_VALUE);
		writeLine(colour, x0 + width, y0 + height, x0 + width, y0, Integer.MAX_VALUE);
	}

	/**
	 * Renders a filled rectangle around a specified coordinate.
	 * @param colour the colour of the circle 
	 * @param width  width of the rectangle to render in pixels
	 * @param height height of the rectangle to render in pixels
	 * @param x 	 screen space x coordinate to render the circle around
	 * @param y 	 screen space y coordinate to render the circle around
	 * @see 		 com.ang.peLib.graphics.PColour
	 */
	public void fillTileAround(PColour colour, int width, int height, int x, int y) {
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

	/**
	 * Renders a filled rectangle around a corner in a sector world.
	 * @param mapData 	  map data containing the sector world with the required corner 
	 * @param params 	  editor params for size, colours, scale, and screen dimensions
	 * @param translation translation of the map relative to the viewport 
	 * @param colour 	  the colour of the circle 
	 * @param sectorIndex index of the sector containing the corner to fill a circle around
	 * @param cornerIndex index of the corner to fill a circle around
	 * @see 			  com.ang.peLib.hittables.PSector
	 * @see 			  com.ang.peLib.hittables.PSectorWorld
	 * @see 			  com.ang.peLib.maths.PVec2
	 * @see 			  com.ang.peLib.graphics.PColour
	 */
	public void fillTileAroundCorner(PPMapData mapData, PEditorParams params, PVec2 translation,
			PColour colour, int sectorIndex, int cornerIndex) {
		PVec2 corner = mapData.world.getSector(sectorIndex).getCorner(cornerIndex);
		int[] coords = PConversions.v2ss(corner, params.width, params.height, 
				params.scale, translation);
		fillTileAround(colour, params.CORNER_SIZE, params.CORNER_SIZE, coords[0], coords[1]);
	}

	/**
	 * Writes the specified mouse coordinates in the top left.
	 * @param x screen space x coordinate of the mouse to show
	 * @param y screen space y coordinate of the mouse to show
	 */
	public void writeMouseCoords(int x, int y) {
		String xString = String.valueOf(x);
		String yString = String.valueOf(y);
		fillTile(PColour.BLACK, 70, 14, 0, 0);
		img.getGraphics().drawString("x:" + xString + " y:" + yString, 0, 10);
	}

	/**
	 * Renders axis lines and a sector world.
	 * Axis lines are only drawn if the scale is not too small, this reduces clutter.
	 * Draws lines for every edge, and circles for every corner.
	 * @param mapData 	  mapdata containing the sector world to render 
	 * @param params 	  editor params for scale, sizes, colours, dimensions
	 * @param translation translation of the map relative to the viewport 
	 */
	public void writeMapData(PPMapData mapData, PEditorParams params, PVec2 translation) {
		fillTile(params.backgroundColour, width, height, 0, 0);	
		if (params.scale > 6.0) {
			writeBackgroundGrid(params.gridColour, params, translation);
		}
		writeXYAxisLines(params.axisColour, params, translation);
		for (PSector sec : mapData.world.getSectors()) {
			PVec2[] corners = sec.getCorners();
			for (int i = 0; i < corners.length; i++) {
				int next = (i < corners.length - 1) ? i + 1 : 0;
				int[] coords = PConversions.v2ss(corners[i], corners[next], 
						width, height, params.scale, translation);
				int dotRate = (sec.isPortal(i, next)) ? 10 : Integer.MAX_VALUE;
				writeLine(params.lineColour, coords[0], coords[1], coords[2], 
						coords[3], dotRate);
			}
			for (int i = 0; i < corners.length; i++) {
				int[] coords = PConversions.v2ss(corners[i], width, height, 
						params.scale, translation);
				fillTileAround(params.cornerColour, params.CORNER_SIZE, 
						params.CORNER_SIZE, coords[0], coords[1]);
			}
		}
		int[] coords = PConversions.v2ss(mapData.position, width, height, 
				params.scale, translation);
		fillCircleAround(PColour.GREEN, 5, coords[0], coords[1]);
	}

	/**
	 * Renders the background axis grid for the editor.
	 * @param colour 	  the colour for the lines of the grid 
	 * @param params 	  editor params for scale, sizes, colours, dimensions
	 * @param translation translation of the map relative to the viewport 
	 */
	public void writeBackgroundGrid(PColour colour, PEditorParams params, PVec2 translation) {
		double[] startCoords = PConversions.ss2v(-width / 4, 0, width, 
				height, params.scale, translation);
		PVec2 startingPoint = new PVec2(Math.floor(startCoords[0]), Math.floor(startCoords[1]));
		for (int i = 0; ; i++) {
			PVec2 linePoint = startingPoint.add(new PVec2(0.0, 1.0 * i));
			int[] coords = PConversions.v2ss(linePoint, width, height, params.scale, translation);
			if (coords[1] > height) break;

			writeLine(colour, 0, coords[1], width, coords[1], Integer.MAX_VALUE);
		}
		for (int i = 0; ; i++) {
			PVec2 linePoint = startingPoint.add(new PVec2(1.0 * i, 0.0));
			int[] coords = PConversions.v2ss(linePoint, width, height, params.scale, translation);
			if (coords[0] > width) break;

			writeLine(colour, coords[0], 0, coords[0], height, Integer.MAX_VALUE);
		}
	}

	/**
	 * Writes central x and y axis lines for the editor background.
	 * @param colour 	  the colour for the lines 
	 * @param params 	  editor params for scale, sizes, colours, dimensions
	 * @param translation translation of the map relative to the viewport 
	 */
	public void writeXYAxisLines(PColour colour, PEditorParams params, PVec2 translation) {
		PVec2 zeroZero = new PVec2(0.0, 0.0);
		int[] coords = PConversions.v2ss(zeroZero, width, height, params.scale, translation);
		writeLine(colour, coords[0], 0, coords[0], height, Integer.MAX_VALUE);
		writeLine(colour, 0, coords[1], width, coords[1], Integer.MAX_VALUE);
	}

	/**
	 * Writes lines to a corner from its surrounding corners inside a sector.
	 * This is used when a corner is being dragged to visualize the change in 
	 * the sector's shape.
	 * @param colour 	  the colour of the lines to draw 
	 * @param point 	  the point to draw the lines towards 
	 * @param sectorIndex index of the sector containing corner that is the target
	 * @param cornerIndex index of the corner that is the target
	 * @param mapData 	  mapdata containing the sectorworld that is being shown
	 * @param params 	  editor params for scale, sizes, colours, dimensions
	 * @param translation translation of the map relative to the viewport 
	 */
	public void writeLinesToCorner(PColour colour, PVec2 point, int sectorIndex, int cornerIndex,
			PPMapData mapData, PEditorParams params, PVec2 translation) {
		PVec2[] corners = mapData.world.getSector(sectorIndex).getCorners();
		int prevIndex = (cornerIndex == 0) ? corners.length - 1 : cornerIndex - 1;
		int nextIndex = (cornerIndex == corners.length - 1) ? 0 : cornerIndex + 1;
		int[] prevCornerCoords = PConversions.v2ss(corners[prevIndex], params.width, 
				params.height, params.scale, translation);
		int[] nextCornerCoords = PConversions.v2ss(corners[nextIndex], params.width, 
				params.height, params.scale, translation);
		int[] currentCornerCoords = PConversions.v2ss(point, params.width, 
				params.height, params.scale, translation);
		writeLine(colour, currentCornerCoords[0], currentCornerCoords[1], 
				prevCornerCoords[0], prevCornerCoords[1], Integer.MAX_VALUE);
		writeLine(colour, currentCornerCoords[0], currentCornerCoords[1], 
				nextCornerCoords[0], nextCornerCoords[1], Integer.MAX_VALUE);
	}
}
