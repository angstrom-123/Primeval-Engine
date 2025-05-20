package com.ang.peEditor;

import com.ang.peEditor.gui.PEditorGUI;
import com.ang.peEditor.gui.PGUIRenderer;
import com.ang.peLib.files.pmap.PPMapHandler;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.utils.PConversions;

public class PGUIRendererHelper {
	private final PEditorParams params;
	private final PGUIRenderer renderer;
	private final PPMapHandler mapHandler;
	private final PEditorGUI gui;

	public PGUIRendererHelper(PEditorParams params, PGUIRenderer renderer, 
			PPMapHandler mapHandler, PEditorGUI gui) {
		this.params = params;
		this.renderer = renderer;
		this.mapHandler = mapHandler;
		this.gui = gui;
	}

	public void refresh(int x, int y) {
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	public void mouseMove(int x, int y, PVec2 translation) {
		refresh(x, y);
	}

	public void mouseMoveShowShadow(int x, int y, double scale, PVec2 translation) {
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
				params, translation);
		int x0 = PConversions.v2ss(0.0, true, params.width, params.height, 
				params.scale, translation);
		int x1 = PConversions.v2ss(scale, true, params.width, 
				params.height, params.scale, translation);
		renderer.writeCircleAround(params.selectedColour2, x1 - x0, x, y);
		refresh(x, y);
	}

	public PVec2 mouseDragSelection(int x, int y, int cornerIndex, int sectorIndex, 
			PVec2 translation) {
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
				params, translation);
		double[] coords = PConversions.ss2v(x, y, params.width, params.height, 
				params.scale, translation);;
		int[] renderCoords = new int[]{x, y};
		PVec2 newPos = new PVec2(coords);
		if (params.snapToGrid) {
			newPos = newPos.round();
			renderCoords = PConversions.v2ss(newPos, params.width, 
					params.height, params.scale, translation);
		}
		renderer.writeLinesToCorner(params.selectedColour, newPos, sectorIndex,
				cornerIndex, mapHandler.getSaveData().editableMapData, params, translation);
		renderer.fillCircleAround(params.selectedColour, params.CORNER_RADIUS, 
				renderCoords[0], renderCoords[1]);
		renderer.fillCircleAroundCorner(mapHandler.getSaveData().editableMapData, params, 
				translation, params.selectedColour, sectorIndex, cornerIndex, params.CORNER_RADIUS);
		return newPos;

	}

	public void mouseReleaseSelection(int x, int y, int cornerIndex, int sectorIndex, 
			PVec2 translation) {
	}
}
