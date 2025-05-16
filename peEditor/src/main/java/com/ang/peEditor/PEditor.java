package com.ang.peEditor;

import java.util.ArrayList;
import java.util.List;

import com.ang.peEditor.history.*;
import com.ang.peEditor.gui.*;
import com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry;
import com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntryType;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.files.pmap.PPMapData;
import com.ang.peLib.files.pmap.PPMapHandler;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.inputs.PMouseInputInterface;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.resources.PModuleName;
import com.ang.peLib.utils.PConversions;

public class PEditor implements PMouseInputInterface, PEditorInterface {
	private int 				newSectorCornerNum;
	private double 				newSectorScale;
	private int 				selectedSectorIndex;
	private int 				selectedCornerIndex;
	private int					lastSectorIndex;
	private int					lastCornerIndex;
	private PVec2				mapTranslation;
	private PVec2				dragStartMapTranslation;
	private int 				dragStartX;
	private int 				dragStartY;
	private PEditorParams 		params;
	private PMouseInputListener mil;
	private PGUIRenderer 		renderer;
	private PPMapHandler 		mapHandler;
	private PEditorGUI			gui;
	private PHistory			history; // TODO: implement undo / redo

	public PEditor() {
		init();
	}

	private void init() {
		newSectorCornerNum		= -1;
		selectedSectorIndex		= -1;
		selectedCornerIndex		= -1;
		lastSectorIndex			= -1;
		lastCornerIndex			= -1;
		mapTranslation			= new PVec2(0.0, 0.0);
		dragStartMapTranslation	= new PVec2(0.0, 0.0);
		dragStartX				= 0;
		dragStartY				= 0;
		params					= new PEditorParams();
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load editor config file from resources");
			e.printStackTrace();
			return;

		}
		mil 				= new PMouseInputListener(this);
		renderer 			= new PGUIRenderer(params.width, params.height, mil);
		mapHandler 			= new PPMapHandler();
		history				= new PHistory(params.historyLength);
		gui 				= new PEditorGUI(params, renderer, this);
		gui.init();
	}

	public void start() {
		System.out.println("Editor running");
		newFile();
	}

	@Override
	public void mouseScrolled(int x, int y, int units) {
		final double step = 0.01;
		params.scale *= (1 + (units * step));
		params.scale = Math.max(params.scale, 0.1);
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
				params, mapTranslation);
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	@Override
	public void mouseMoved(int x, int y) {
		if (newSectorCornerNum != -1) {
			renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
					params, mapTranslation);
			int x0 = PConversions.v2ss(0.0, true, params.width, params.height, 
					params.scale, mapTranslation);
			int x1 = PConversions.v2ss(newSectorScale, true, params.width, 
					params.height, params.scale, mapTranslation);
			renderer.writeCircleAround(params.selectedColour2, x1 - x0, x, y);
		}
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	@Override
	public void mouseDragged(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		if ((selectedSectorIndex != -1) && (selectedCornerIndex != -1)) {
			renderer.writeMapData(editableData, params, mapTranslation);
			double[] coords = PConversions.ss2v(x, y, params.width, 
					params.height, params.scale, mapTranslation);;
			PVec2 newPos = new PVec2(coords);
			if (params.snapToGrid) {
				newPos = newPos.round();
				int[] snappedCoords = PConversions.v2ss(newPos, params.width, 
						params.height, params.scale, mapTranslation);
				renderer.writeLinesToCorner(params.selectedColour, newPos, selectedSectorIndex,
						selectedCornerIndex, editableData, params, mapTranslation);
				renderer.fillCircleAround(params.selectedColour, params.CORNER_RADIUS, 
						snappedCoords[0], snappedCoords[1]);
				gui.openDataPanel(getDataForDragged(newPos.x(), newPos.y()));
				renderer.fillCircleAroundCorner(editableData, params, mapTranslation, 
						params.selectedColour, lastSectorIndex, lastCornerIndex, 
						params.CORNER_RADIUS);
			} else {
				renderer.writeLinesToCorner(params.selectedColour, newPos, selectedSectorIndex,
						selectedCornerIndex, editableData, params, mapTranslation);
				renderer.fillCircleAround(params.selectedColour, params.CORNER_RADIUS, x, y);
			}
		} else {
			renderer.writeMapData(editableData,	params, mapTranslation);
			PVec2 endPos = new PVec2(PConversions.ss2v(x, y, params.width, params.height,
					params.scale, mapTranslation));
			PVec2 startPos = new PVec2(PConversions.ss2v(dragStartX, dragStartY, params.width, params.height,
					params.scale, mapTranslation));
			PVec2 delta = endPos.sub(startPos);
			mapTranslation = dragStartMapTranslation.add(delta);
		}
		gui.closeRMBPanels();
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
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
			PVec2 newPos = new PVec2(coords);
			if (params.snapToGrid) {
				newPos = newPos.round();
			}
			PSector selectedSec = editableData.world.getSector(selectedSectorIndex);
			selectedSec.getCorners()[selectedCornerIndex] = newPos;
			gui.openDataPanel(getDataForSelected());
		}
		if (newSectorCornerNum != -1) {
			PSector sec = PSectorFactory.newSector(newSectorCornerNum, newSectorScale,
					new PVec2(coords[0], coords[1]));
			if (sec != null) {
				editableData.world.addSector(sec);
			}
			newSectorCornerNum = -1;
		}
		renderer.writeMapData(editableData, params, mapTranslation);
		if ((lastSectorIndex != -1) && (lastCornerIndex != -1)) {
			renderer.fillCircleAroundCorner(editableData, params, mapTranslation, 
					params.selectedColour, lastSectorIndex, lastCornerIndex,
					params.CORNER_RADIUS);
		}
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
	public void dataPanelChange(PDataPanelEntry entry, String text) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		PSector sector = editableData.world.getSector(entry.sectorIndex);
		switch (entry.entryType) {
		case CORNER_INDEX: // readonly
		case SECTOR_INDEX: // readonly
			return;
		case CORNER_X: 
			{
			double val = Double.valueOf(text);
			PVec2 newPos = new PVec2(val, sector.getCorner(entry.cornerIndex).y());
			sector.getCorners()[entry.cornerIndex] = newPos;
			}
			break;

		case CORNER_Y:
			{
			double val = Double.valueOf(text);
			PVec2 newPos = new PVec2(sector.getCorner(entry.cornerIndex).x(), val);
			sector.getCorners()[entry.cornerIndex] = newPos;
			}
			break;

		case CORNER_IS_PORTAL:
			boolean isPortal = Boolean.valueOf(text);
			sector.setAsPortal(entry.cornerIndex, isPortal);
			break;

		case SECTOR_FLOOR:
			{
			double val = Double.valueOf(text);
			sector.setHeight(val, sector.getCeilingHeight());
			}
			break;

		case SECTOR_CEILING:
			{
			double val = Double.valueOf(text);
			sector.setHeight(sector.getFloorHeight(), val);
			}
			break;

		case SECTOR_LIGHT:
			{
			double val = Double.valueOf(text);
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
		System.out.println("Deleting sector of index: " + sectorIndex);
		gui.closeRMBPanels();
		renderer.repaint();
	}

	@Override
	public void delCorner(int cornerIndex, int sectorIndex) {
		System.out.println("Deleting corner at indices c:" + cornerIndex + " s:" + sectorIndex);
		gui.closeRMBPanels();
		renderer.repaint();
	}

	@Override
	public void insCornerLeft(int cornerIndex, int sectorIndex) {
		System.out.println("Inserting corner left at indices c:" + cornerIndex + " s:" + sectorIndex);
		gui.closeRMBPanels();
		renderer.repaint();
	}

	@Override
	public void insCornerRight(int cornerIndex, int sectorIndex) {
		System.out.println("Inserting corner right at indices c:" + cornerIndex + " s:" + sectorIndex);
		gui.closeRMBPanels();
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
		int topPanel = PDataPanelEntry.TOP;
		int botPanel = PDataPanelEntry.BOT;
		int i = lastSectorIndex;
		int j = lastCornerIndex;
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_INDEX, topPanel, 
									lastCornerIndex, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_X, topPanel, 
									x, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_Y, topPanel, 
									y, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_IS_PORTAL, topPanel, 
									sec.isPortal(lastCornerIndex), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_INDEX, botPanel, 
									lastSectorIndex, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_FLOOR, botPanel, 
									sec.getFloorHeight(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_CEILING, botPanel, 
									sec.getCeilingHeight(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_LIGHT, botPanel, 
									sec.getLightLevel(), i, j));
		return out;

	}

	private List<PDataPanelEntry> getDataForSelected() {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		PSector sec = mapHandler.getSaveData().editableMapData
					.world.getSector(lastSectorIndex);
		PVec2 corner = sec.getCorner(lastCornerIndex);
		int topPanel = PDataPanelEntry.TOP;
		int botPanel = PDataPanelEntry.BOT;
		int i = lastSectorIndex;
		int j = lastCornerIndex;
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_INDEX, topPanel, 
									lastCornerIndex, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_X, topPanel, 
									corner.x(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_Y, topPanel, 
									corner.y(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_IS_PORTAL, topPanel, 
									sec.isPortal(lastCornerIndex), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_INDEX, botPanel, 
									lastSectorIndex, i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_FLOOR, botPanel, 
									sec.getFloorHeight(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_CEILING, botPanel, 
									sec.getCeilingHeight(), i, j));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_LIGHT, botPanel, 
									sec.getLightLevel(), i, j));
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
