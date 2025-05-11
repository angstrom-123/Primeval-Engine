package com.ang.peEditor;

import java.util.ArrayList;
import java.util.List;

import com.ang.peEditor.history.*;
import com.ang.peEditor.dataPanel.PDataPanelEntry;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.files.pmap.PPMapHandler;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.inputs.PMouseInputInterface;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.resources.PModuleName;
import com.ang.peLib.utils.PConversions;

public class PEditor implements PMouseInputInterface, PEditorInterface {
	private int 				selectedSectorIndex;
	private int 				selectedCornerIndex;
	private int					lastSectorIndex;
	private int					lastCornerIndex;
	private PVec2 				viewPos;
	private PVec2 				viewPosAtDragStart;
	private PVec2 				dragStartPos;
	private PEditorParams 		params;
	private PMouseInputListener mil;
	private PGUIRenderer 		renderer;
	private PPMapHandler 		mapHandler;
	private PEditorGUI			gui;
	private PHistory			history;

	public PEditor() {
		init();
	}

	private void init() {
		selectedSectorIndex = -1;
		selectedCornerIndex = -1;
		lastSectorIndex 	= -1;
		lastCornerIndex 	= -1;
		viewPos 			= new PVec2(0.0, 0.0);
		viewPosAtDragStart 	= new PVec2(0.0, 0.0);
		dragStartPos 		= new PVec2(0.0, 0.0);
		params 				= new PEditorParams();
		mil 				= new PMouseInputListener(this);
		renderer 			= new PGUIRenderer(params.width, params.height, mil);
		mapHandler 			= new PPMapHandler();
		history				= new PHistory(params.historyLength);
		gui 				= new PEditorGUI(renderer, this);
		gui.init();
	}

	public void start() {
		System.out.println("Editor running");
	}

	@Override
	public void mouseScrolled(int units) {
		final double step = 0.05;
		params.scale *= (1 + (units * step));
		params.scale = Math.max(params.scale, 0.1);
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
				params, viewPos);
		renderer.repaint();
	}

	@Override
	public void mouseMoved(int x, int y) {
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	@Override
	public void mouseDragged(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		if ((selectedSectorIndex != -1) && (selectedCornerIndex != -1)) {
			renderer.writeMapData(editableData, params, viewPos);
			double[] coords = PConversions.ss2v(x, y, params.height, 
					params.width, params.scale, viewPos);;
			PVec2 newPos = new PVec2(coords);
			if (params.snapToGrid) {
				newPos = newPos.round();
				int[] snappedCoords = PConversions.v2ss(newPos, params.width, 
						params.height, params.scale, viewPos);
				renderer.writeLinesToCorner(params.selectedColour, newPos, selectedSectorIndex,
						selectedCornerIndex, editableData, params, viewPos);
				renderer.writeTileAround(params.selectedColour, params.CORNER_SIZE, 
						params.CORNER_SIZE, snappedCoords[0], snappedCoords[1]);
			} else {
				renderer.writeLinesToCorner(params.selectedColour, newPos, selectedSectorIndex,
						selectedCornerIndex, editableData, params, viewPos);
				renderer.writeTileAround(params.selectedColour, params.CORNER_SIZE, 
						params.CORNER_SIZE, x, y);
			}
		} else {
			renderer.writeMapData(editableData,	params, viewPos);
			int dx = (int) dragStartPos.x() - x;
			int dy = (int) dragStartPos.y() - y;
			viewPos = viewPosAtDragStart.sub(new PVec2(dx, dy));
		}
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	@Override
	public void mousePressed(int x, int y) {
		boolean found = findSelectedCorner(x, y);
		lastSectorIndex = selectedSectorIndex;
		lastCornerIndex = selectedCornerIndex;
		if (!found) {
			dragStartPos = new PVec2(x, y);
			viewPosAtDragStart = viewPos.copy();
			gui.closeDataPanel();
		}
	}

	@Override
	public void mouseReleased(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		if ((selectedSectorIndex != -1) && (selectedCornerIndex != -1)) {
			double[] coords = PConversions.ss2v(x, y, params.height, 
					params.width, params.scale, viewPos);;
			PVec2 newPos = new PVec2(coords);
			if (params.snapToGrid) {
				newPos = newPos.round();
			}
			PSector selectedSec = editableData.world.getSectors()[selectedSectorIndex];
			selectedSec.getCorners()[selectedCornerIndex] = newPos;
			gui.openDataPanel(getDataForSelected());
		}
		renderer.writeMapData(editableData, params, viewPos);
		dragStartPos = new PVec2(0.0, 0.0);
		selectedSectorIndex = -1;
		selectedCornerIndex = -1;
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	@Override
	public void mouseExited() {

	}

	@Override
	public void newFile() {
		reset();
		PPMapData temp = new PPMapData();
		temp.world = new PSectorWorld(1000);
		temp.facing = new PVec2(0.0, 0.0);
		temp.position = new PVec2(0.0, 0.0);
		mapHandler.setEditableMapData(temp);
		mapHandler.setSavedMapData(temp.copy());
		renderer.writeMapData(temp, params, viewPos);
		renderer.repaint();
	}

	@Override 
	public void open(String name) {
		reset();
		try {
			mapHandler.loadMapData(name, PModuleName.EDITOR);
		} catch (PResourceException e) {
			e.printStackTrace();
		}
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		renderer.writeMapData(editableData, params, viewPos);
		renderer.repaint();
	}

	@Override 
	public void save(String name) {
		try {
			mapHandler.saveMap(name, PModuleName.EDITOR);
			mapHandler.saveMap(name, PModuleName.CORE);
		} catch (PResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exit() {
		renderer.close();
	}

	@Override 
	public void undo() {
		System.out.println("undo");
	}

	@Override 
	public void redo() {
		System.out.println("redo");
	}

	@Override 
	public void newSector() {
		System.out.println("new sector");
	}

	@Override 
	public void newCorner() {
		System.out.println("new corner");
	}

	private void reset() {
		params.init();
		selectedSectorIndex = -1;
		selectedCornerIndex = -1;
		viewPos 			= new PVec2(0.0, 0.0);
		viewPosAtDragStart 	= new PVec2(0.0, 0.0);
		dragStartPos 		= new PVec2(0.0, 0.0);
	}

	private List<PDataPanelEntry> getDataForSelected() {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		PSector sec = mapHandler.getSaveData().editableMapData
				.world.getSectors()[lastSectorIndex];
		PVec2 corner = sec.getCorners()[lastCornerIndex];
		out.add(new PDataPanelEntry(PDataPanelEntry.TOP, "Index", lastCornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntry.TOP, "x", corner.x()));
		out.add(new PDataPanelEntry(PDataPanelEntry.TOP, "y", corner.y()));
		out.add(new PDataPanelEntry(PDataPanelEntry.TOP, "Portal", sec.isPortal(lastCornerIndex)));
		out.add(new PDataPanelEntry(PDataPanelEntry.BOT, "Index", lastSectorIndex));
		out.add(new PDataPanelEntry(PDataPanelEntry.BOT, "Floor y", sec.getFloorHeight()));
		out.add(new PDataPanelEntry(PDataPanelEntry.BOT, "Ceiling y", sec.getCeilingHeight()));
		out.add(new PDataPanelEntry(PDataPanelEntry.BOT, "Light Level", sec.getLightLevel()));
		return out;

	}

	private boolean findSelectedCorner(int x, int y) {
		final PSector[] sectors = mapHandler.getSaveData().editableMapData
				.world.getSectors();
		final int leeway = (int) Math.round((params.CORNER_SIZE / 2) * 2.5);
		for (int i = 0; i < sectors.length; i++) {
			PVec2[] corners = sectors[i].getCorners();
			for (int j = 0; j < corners.length; j++) {
				int[] coords = PConversions.v2ss(corners[j], params.width, 
						params.height, params.scale, viewPos);
				int minX = coords[0] - leeway;
				int maxX = coords[0] + leeway;
				int minY = coords[1] - leeway;
				int maxY = coords[1] + leeway;
				if ((x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY)) {
					selectedSectorIndex = i;
					selectedCornerIndex = j;
					return true;

				}
			}
		}
		selectedSectorIndex = -1;
		selectedCornerIndex = -1;
		return false;

	}
}
