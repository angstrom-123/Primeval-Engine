package com.ang.peEditor;

import java.util.ArrayList;
import java.util.List;

import com.ang.peEditor.history.*;
import com.ang.peEditor.gui.*;
import com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry;
import com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntryFactory;
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
	private int 				newSectorCornerNum		= -1;
	private double 				newSectorScale			= -1;
	private int 				selectedSectorIndex		= -1;
	private int 				selectedCornerIndex		= -1;
	private int					lastSectorIndex			= -1;
	private int					lastCornerIndex			= -1;
	private PVec2				mapTranslation			= new PVec2(0.0, 0.0);
	private PVec2				dragStartMapTranslation = new PVec2(0.0, 0.0);
	private int 				dragStartX				= 0;
	private int 				dragStartY 				= 0;
	private PEditorParams 		params;
	private PMouseInputListener mil;
	private PGUIRenderer 		renderer;
	private PPMapHandler 		mapHandler;
	private PEditorGUI			gui;
	private PGUIRendererHelper  rendererHelper;
	private PHistory			history;

	public PEditor() {
		init();
	}

	private void init() {
		params = new PEditorParams();
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load editor config file from resources");
			e.printStackTrace();
			return;

		}
		mil = new PMouseInputListener(this);
		renderer = new PGUIRenderer(params.width, params.height, mil);
		mapHandler = new PPMapHandler();
		gui = new PEditorGUI(params, renderer, this);
		gui.init();
		rendererHelper = new PGUIRendererHelper(params, renderer, mapHandler, gui);
		history	= new PHistory(params.historyLength);
	}

	public void start() {
		System.out.println("Editor running");
		newFile();
	}

	@Override
	public void mouseScrolled(int x, int y, int units) {
		rendererHelper.mouseScroll(x, y, units, mapTranslation);
	}

	@Override
	public void mouseMoved(int x, int y) {
		if (newSectorCornerNum == -1) {
			rendererHelper.mouseMove(x, y, mapTranslation);
		} else {
			rendererHelper.mouseMoveShowShadow(x, y, newSectorScale, mapTranslation);
		}
	}

	@Override
	public void mouseDragged(int x, int y) {
		if ((selectedSectorIndex != -1) && (selectedCornerIndex != -1)) {
			PVec2 draggedPos = rendererHelper.mouseDragSelection(x, y, selectedCornerIndex,
					selectedSectorIndex, mapTranslation);
			gui.openDataPanel(getDataForDragged(draggedPos.x(), draggedPos.y()));
		} else {
			PVec2 startPos = new PVec2(PConversions.ss2v(dragStartX, dragStartY, 
					params.width, params.height, params.scale, mapTranslation));
			PVec2 endPos = new PVec2(PConversions.ss2v(x, y, params.width, params.height,
					params.scale, mapTranslation));
			PVec2 delta = endPos.sub(startPos);
			mapTranslation = dragStartMapTranslation.add(delta);
			renderer.writeMapData(mapHandler.getSaveData().editableMapData,	params, 
					mapTranslation);
		}
		gui.closeRMBPanels();
		rendererHelper.refresh(x, y);
	}

	@Override
	public void mousePressed(int x, int y) {
		boolean found = findSelectedCorner(x, y);
		lastSectorIndex = selectedSectorIndex;
		lastCornerIndex = selectedCornerIndex;
		if (!found) {
			dragStartX = x;
			dragStartY = y;
			dragStartMapTranslation = mapTranslation.copy();
			gui.closeDataPanels();
		}
		gui.closeRMBPanels();
	}

	@Override
	public void rightMousePressed(int x, int y) {
		boolean found = findSelectedCorner(x, y);
		lastSectorIndex = selectedSectorIndex;
		lastCornerIndex = selectedCornerIndex;
		if (found) {
			gui.openRightClickMenu(x, y, lastSectorIndex, lastCornerIndex);
		}
		newSectorScale = -1.0;
		newSectorCornerNum = -1;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	@Override
	public void mouseReleased(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		double[] coords = PConversions.ss2v(x, y, params.width, 
				params.height, params.scale, mapTranslation);
		if ((selectedSectorIndex != -1) && (selectedCornerIndex != -1)) {
			moveSelectedCornerTo(coords);
		}
		if (newSectorCornerNum != -1) {
			insertNewSectorAt(coords);
		}
		renderer.writeMapData(editableData, params, mapTranslation);
		if ((lastSectorIndex != -1) && (lastCornerIndex != -1)) {
			renderer.fillCircleAroundCorner(editableData, params, mapTranslation, 
					params.selectedColour, lastSectorIndex, lastCornerIndex,params.CORNER_RADIUS);
		}
		selectedSectorIndex = -1;
		selectedCornerIndex = -1;
		rendererHelper.refresh(x, y);
	}

	private void moveSelectedCornerTo(double[] coords) {
		PVec2 newPos = new PVec2(coords);
		if (params.snapToGrid) {
			newPos = newPos.round();
		}
		PSector sec = mapHandler.getSaveData().editableMapData.world
				.getSector(selectedSectorIndex);
		// move corner
		sec.getCorners()[selectedCornerIndex] = newPos;
		gui.openDataPanel(getDataForSelected());
	}

	private void insertNewSectorAt(double[] coords) {
		PSector sec = PSectorFactory.newSector(newSectorCornerNum, newSectorScale,
				new PVec2(coords[0], coords[1]));
		if (sec != null) {
			// insert sector
			int index = mapHandler.getSaveData().editableMapData.world.addSector(sec);
		}
		newSectorCornerNum = -1;
	}

	@Override
	public void rightMouseReleased(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		if ((lastSectorIndex != -1) && (lastCornerIndex != -1)) {
			renderer.fillCircleAroundCorner(editableData, params, mapTranslation, 
					params.selectedColour, lastSectorIndex, lastCornerIndex,
					params.CORNER_RADIUS);
			renderer.repaint();
		}
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
		renderer.writeMapData(temp, params, mapTranslation);
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
		renderer.writeMapData(editableData, params, mapTranslation);
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
	public void newSector(int cornerCount, double scale) {
		newSectorCornerNum = cornerCount;
		newSectorScale = scale;
	}

	@Override
	public void dataPanelChange(PDataPanelEntry panelEntry, String text) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		PSector sector = editableData.world.getSector(panelEntry.sectorIndex);
		switch (panelEntry.entryType) {
		case CORNER_INDEX: // readonly
		case SECTOR_INDEX: // readonly
			return;

		case CORNER_X: 
			{
			double val = Double.valueOf(text);
			PVec2 newPos = new PVec2(val, sector.getCorner(panelEntry.cornerIndex).y());
			// move corner
			sector.getCorners()[panelEntry.cornerIndex] = newPos;
			}
			break;

		case CORNER_Y:
			{
			double val = Double.valueOf(text);
			PVec2 newPos = new PVec2(sector.getCorner(panelEntry.cornerIndex).x(), val);
			// move corner
			sector.getCorners()[panelEntry.cornerIndex] = newPos;
			}
			break;

		case CORNER_IS_PORTAL:
			{
			boolean isPortal = Boolean.valueOf(text);
			// set as portal
			sector.setAsPortal(panelEntry.cornerIndex, isPortal);
			}
			break;

		case SECTOR_FLOOR:
			{
			double val = Double.valueOf(text);
			// change floor height
			sector.setHeight(val, sector.getCeilingHeight());
			}
			break;

		case SECTOR_CEILING:
			{
			double val = Double.valueOf(text);
			// change ceiling height
			sector.setHeight(sector.getFloorHeight(), val);
			}
			break;

		case SECTOR_LIGHT:
			{
			double val = Double.valueOf(text);
			// change light level
			sector.setLightLevel(val);
			}
			break;

		}
		renderer.writeMapData(editableData, params, mapTranslation);
		if ((lastSectorIndex != -1) && (lastCornerIndex != -1)) {
			renderer.fillTileAroundCorner(editableData, params, mapTranslation, 
					params.selectedColour2, lastSectorIndex, lastCornerIndex);
		}
		renderer.repaint();
	}

	@Override
	public void delSector(int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		// remove sector
		editableData.world.removeSectorAt(sectorIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	@Override
	public void delCorner(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		// remove corner
		editableData.world.getSectors()[sectorIndex].removeCornerAt(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	@Override
	public void insCornerLeft(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		// insert corner
		int newCornerIndex = editableData.world.getSectors()[sectorIndex].
			insertCornerBefore(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	@Override
	public void insCornerRight(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		// insert corner
		int newCornerIndex = editableData.world.getSectors()[sectorIndex].
				insertCornerAfter(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}


	private void reset() {
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load editor config file from resources");
			e.printStackTrace();
			exit();
			return;

		}
		selectedSectorIndex		= -1;
		selectedCornerIndex		= -1;
		mapTranslation			= new PVec2(0.0, 0.0);
		dragStartMapTranslation	= new PVec2(0.0, 0.0);
		dragStartX				= 0;
		dragStartY				= 0;
	}

	private List<PDataPanelEntry> getDataForDragged(double x, double y) {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		PSector sec = mapHandler.getSaveData().editableMapData
					.world.getSector(lastSectorIndex);
		out.addAll(PDataPanelEntryFactory.newDefaultTopEntries(new PVec2(x, y), sec, 
					lastCornerIndex, lastSectorIndex));
		out.addAll(PDataPanelEntryFactory.newDefaultBottomEntries(sec, 
					lastCornerIndex, lastSectorIndex));
		return out;

	}

	private List<PDataPanelEntry> getDataForSelected() {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		PSector sec = mapHandler.getSaveData().editableMapData
					.world.getSector(lastSectorIndex);
		PVec2 corner = sec.getCorner(lastCornerIndex);
		out.addAll(PDataPanelEntryFactory.newDefaultTopEntries(corner, sec, 
					lastCornerIndex, lastSectorIndex));
		out.addAll(PDataPanelEntryFactory.newDefaultBottomEntries(sec, 
					lastCornerIndex, lastSectorIndex));
		return out;

	}

	private boolean findSelectedCorner(int x, int y) {
		final PSector[] sectors = mapHandler.getSaveData().editableMapData
				.world.getSectors();
		final int leeway = (int) Math.round((params.CORNER_SIZE / 2) * 3.0);
		for (int i = 0; i < sectors.length; i++) {
			PVec2[] corners = sectors[i].getCorners();
			for (int j = 0; j < corners.length; j++) {
				int[] coords = PConversions.v2ss(corners[j], params.width, 
						params.height, params.scale, mapTranslation);
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
