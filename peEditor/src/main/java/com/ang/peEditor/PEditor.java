package com.ang.peEditor;

import java.util.ArrayList;
import java.util.List;

import com.ang.peEditor.history.*;
import com.ang.peEditor.gui.*;
import com.ang.peEditor.gui.menu.dataMenu.*;
import com.ang.peLib.files.pmap.*;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.inputs.PFullKeyboardInputListener;
import com.ang.peLib.inputs.PMouseInputInterface;
import com.ang.peLib.inputs.PMouseInputListener;
import com.ang.peLib.maths.PVec2;
import com.ang.peLib.resources.PModuleName;
import com.ang.peLib.utils.PConversions;

/**
 * Main class for running the editor.
 */
public class PEditor implements PMouseInputInterface, PEditorInterface {
	private int newSectorCornerNum = -1;
	private double newSectorScale = -1;
	private int selSecIndex	= -1;
	private int selCorIndex	= -1;
	private int	lastSectorIndex	= -1;
	private int	lastCornerIndex = -1;
	private PVec2 mapTranslation = new PVec2(0.0, 0.0);
	private PVec2 dragStartMapTranslation = new PVec2(0.0, 0.0);
	private int dragStartX = 0;
	private int dragStartY = 0;
	private PEditorParams params;
	private PFullKeyboardInputListener kil;
	private PMouseInputListener mil;
	private PShortcutHandler shortcutHandler;
	private PGUIRenderer renderer;
	private PPMapHandler mapHandler;
	private PEditorGUI gui;
	private PGUIRendererHelper rendererHelper;
	private PHistory history;
	private PHistory redoHistory;

	/**
	 * Constructs.
	 */
	public PEditor() {
		init();
	}

	/**
	 * Initializes editor objects, attempts to read config file.
	 */
	private void init() {
		params = new PEditorParams();
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load editor config file from resources");
			e.printStackTrace();
			return;

		}
		shortcutHandler = new PShortcutHandler(this);
		kil = new PFullKeyboardInputListener(shortcutHandler);
		mil = new PMouseInputListener(this);
		renderer = new PGUIRenderer(params.width, params.height, kil, mil);
		mapHandler = new PPMapHandler();
		gui = new PEditorGUI(params, renderer, this);
		gui.init();
		rendererHelper = new PGUIRendererHelper(params, renderer, mapHandler);
		history	= new PHistory(params.historyLength);
		redoHistory	= new PHistory(params.historyLength);
	}

	/**
	 * Runs the editor.
	 */
	public void start() {
		System.out.println("Editor running");
		newFile();
	}

	/**
	 * Handles the mouse scroll event by zooming the map in and out.
	 * @param x     screen space x coordinate of the mouse
	 * @param y     screen space y coordinate of the mouse
	 * @param units amount scrolled
	 */
	@Override
	public void mouseScrolled(int x, int y, int units) {
		final double step = 0.08;
		params.scale *= (1 + (units * step));
		params.scale = Math.max(params.scale, 0.1);
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, 
				params, mapTranslation);
		renderer.writeMouseCoords(x, y);
		renderer.repaint();
	}

	/**
	 * Handles the mouse moved event by rendering the mouse coordinates.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 */
	@Override
	public void mouseMoved(int x, int y) {
		if (newSectorCornerNum == -1) {
			rendererHelper.mouseMove(x, y, mapTranslation);
		} else {
			rendererHelper.mouseMoveShowShadow(x, y, newSectorScale, mapTranslation);
		}
	}

	/**
	 * Handles the mouse dragged event by moving corners or panning the view.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 */
	@Override
	public void mouseDragged(int x, int y) {
		if ((selSecIndex != -1) && (selCorIndex != -1)) {
			PVec2 draggedPos = rendererHelper.mouseDragSelection(x, y, selCorIndex,
					selSecIndex, mapTranslation);
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

	/**
	 * Handles the mouse pressed event by selecting or deselecting a corner.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 */
	@Override
	public void mousePressed(int x, int y) {
		boolean found = findSelectedCorner(x, y);
		lastSectorIndex = selSecIndex;
		lastCornerIndex = selCorIndex;
		if (!found) {
			dragStartX = x;
			dragStartY = y;
			dragStartMapTranslation = mapTranslation.copy();
			gui.closeDataPanels();
		}
		gui.closeRMBPanels();
	}

	/**
	 * Handles the right click event by opening a menu.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 * @see   com.ang.peEditor.gui.menu.rmbMenu.PRMBPanel
	 */
	@Override
	public void rightMousePressed(int x, int y) {
		boolean found = findSelectedCorner(x, y);
		lastSectorIndex = selSecIndex;
		lastCornerIndex = selCorIndex;
		if (found) {
			gui.openRightClickMenu(x, y, lastSectorIndex, lastCornerIndex);
		}
		newSectorScale = -1.0;
		newSectorCornerNum = -1;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the mouse released event by updating moved corners and inserted sectors.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 */
	@Override
	public void mouseReleased(int x, int y) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		double[] coords = PConversions.ss2v(x, y, params.width, 
				params.height, params.scale, mapTranslation);
		if ((selSecIndex != -1) && (selCorIndex != -1)) {
			history.push(editableData.world.copy());
			moveSelectedCornerTo(coords);
		}
		if (newSectorCornerNum != -1) {
			history.push(editableData.world.copy());
			insertNewSectorAt(coords);
		}
		renderer.writeMapData(editableData, params, mapTranslation);
		if ((lastSectorIndex != -1) && (lastCornerIndex != -1)) {
			renderer.fillCircleAroundCorner(editableData, params, mapTranslation, 
					params.selectedColour, lastSectorIndex, lastCornerIndex,params.CORNER_RADIUS);
		}
		selSecIndex = -1;
		selCorIndex = -1;
		rendererHelper.refresh(x, y);
	}

	/**
	 * Moves the selected corner to specified world space coordinates.
	 * @param coords x and y world space coordinates to move the selected corner to 
	 */
	private void moveSelectedCornerTo(double[] coords) {
		PVec2 newPos = new PVec2(coords);
		if (params.snapToGrid) {
			newPos = newPos.roundToHalf();
		}
		PSector sec = mapHandler.getSaveData().editableMapData.world
				.getSector(selSecIndex);
		sec.getCorners()[selCorIndex] = newPos;
		gui.openDataPanel(getDataForSelected());
	}

	/**
	 * Inserts a new sector at the specified coordinates.
	 * @param coords x and y world space coordinates to insert a new sector around
	 */
	private void insertNewSectorAt(double[] coords) {
		PSector sec = PSectorFactory.newSector(newSectorCornerNum, newSectorScale,
				new PVec2(coords[0], coords[1]));
		if (sec != null) {
			mapHandler.getSaveData().editableMapData.world.addSector(sec);
		}
		newSectorCornerNum = -1;
	}

	/**
	 * Handles the right click released event by cancelling new sector insertion.
	 * @param x screen space x coordinate of the mouse
	 * @param y screen space y coordinate of the mouse
	 */
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

	/**
	 * Handles the mouse exited event.
	 * Currently does nothing
	 */
	@Override
	public void mouseExited() {

	}

	/**
	 * Handles the change position event by setting the position of the player in the map.
	 * @param x world space x coordinate of the player
	 * @param y world space y coordinate of the player
	 */
	@Override
	public void changePosition(double x, double y) {
		PVec2 pos = new PVec2(x, y);
		mapHandler.getSaveData().editableMapData.position = pos;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the change facing event by setting the facing vector of the player in the map.
	 * @param x world space x component of the facing vector
	 * @param y world space y component of the facing vector
	 */
	@Override
	public void changeFacing(double x, double y) {
		PVec2 facing = new PVec2(x, y);
		facing = facing.unitVector();
		mapHandler.getSaveData().editableMapData.facing = facing;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the new file event by creating a new blank map file to edit.
	 */
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

	/**
	 * Handles the open event by loading the map with the specified name for editing.
	 * @param name name of the map file to load
	 */
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

	/**
	 * Handles the save event by saving the currently editable map under the 
	 * specified name.
	 * If the name specified already exists, the old file is automatically overwritten
	 * @param name name of the file to save the current map data in
	 */
	@Override 
	public void save(String name) {
		try {
			mapHandler.saveMap(name, PModuleName.EDITOR);
			mapHandler.saveMap(name, PModuleName.CORE);
		} catch (PResourceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the exit event by closing the editor window.
	 */
	@Override
	public void exit() {
		renderer.close();
	}

	/**
	 * Handles the undo event by loading back the most recent entry in the history.
	 * @see com.ang.peEditor.history.PHistory
	 */
	@Override 
	public void undo() {
		PSectorWorld entry = history.pop();
		if (entry == null) return;

		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		redoHistory.push(editableData.world.copy());
		editableData.world = entry;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the redo event by loading back the most recent entry in the redo history.
	 * @see com.ang.peEditor.history.PHistory
	 */
	@Override 
	public void redo() {
		PSectorWorld entry = redoHistory.pop();
		if (entry == null) return;

		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		history.push(editableData.world.copy());
		editableData.world = entry;
		renderer.writeMapData(mapHandler.getSaveData().editableMapData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the new sector event by initializing parameters for a new sector.
	 * @param cornerCount the amount of corners the new sector should have 
	 * @param scale 	  the scale of the sector to insert
	 */
	@Override 
	public void newSector(int cornerCount, double scale) {
		newSectorCornerNum = cornerCount;
		newSectorScale = scale;
	}

	/**
	 * Handles the data panel (right side) change event by updating sector properties.
	 * @param panelEntry the entry that changed in the data panel 
	 * @param text  	 the new value of the panel entry
	 * @see 			 com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 */
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
			history.push(editableData.world.copy());
			// move corner
			sector.getCorners()[panelEntry.cornerIndex] = newPos;
			}
			break;

		case CORNER_Y:
			{
			double val = Double.valueOf(text);
			PVec2 newPos = new PVec2(sector.getCorner(panelEntry.cornerIndex).x(), val);
			history.push(editableData.world.copy());
			// move corner
			sector.getCorners()[panelEntry.cornerIndex] = newPos;
			}
			break;

		case CORNER_IS_PORTAL:
			{
			boolean isPortal = Boolean.valueOf(text);
			history.push(editableData.world.copy());
			// set as portal
			sector.setAsPortal(panelEntry.cornerIndex, isPortal);
			}
			break;

		case SECTOR_FLOOR:
			{
			double val = Double.valueOf(text);
			history.push(editableData.world.copy());
			// change floor height
			sector.setHeight(val, sector.getCeilingHeight());
			}
			break;

		case SECTOR_CEILING:
			{
			double val = Double.valueOf(text);
			history.push(editableData.world.copy());
			// change ceiling height
			sector.setHeight(sector.getFloorHeight(), val);
			}
			break;

		case SECTOR_LIGHT:
			{
			double val = Double.valueOf(text);
			history.push(editableData.world.copy());
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

	/**
	 * Handles the delete sector event by deleting a specified sector.
	 * @param sectorIndex index of the sector to delete
	 * @see   com.ang.peLib.hittables.PSector
	 */
	@Override
	public void delSector(int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		history.push(editableData.world.copy());
		// remove sector
		editableData.world.removeSectorAt(sectorIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the delete corner event by deleting a specified corner.
	 * @param cornerIndex index of the corner to delete
	 * @param sectorIndex index of the sector containing the corner to delete
	 * @see   com.ang.peLib.maths.PVec2
	 */
	@Override
	public void delCorner(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		history.push(editableData.world.copy());
		// remove corner
		editableData.world.getSectors()[sectorIndex].removeCornerAt(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the insert corner event by inserting a corner before a given index.
	 * @param cornerIndex index of the corner to insert a new corner before 
	 * @param sectorIndex index of the sector to add the corner in
	 */
	@Override
	public void insCornerLeft(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		history.push(editableData.world.copy());
		// insert corner
		editableData.world.getSectors()[sectorIndex].insertCornerBefore(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}

	/**
	 * Handles the insert corner event by inserting a corner after a given index.
	 * @param cornerIndex index of the corner to insert a new corner after
	 * @param sectorIndex index of the sector to add the corner in
	 */
	@Override
	public void insCornerRight(int cornerIndex, int sectorIndex) {
		PPMapData editableData = mapHandler.getSaveData().editableMapData;
		history.push(editableData.world.copy());
		// insert corner
		editableData.world.getSectors()[sectorIndex].insertCornerAfter(cornerIndex);
		// update gui
		gui.closeRMBPanels();
		renderer.writeMapData(editableData, params, mapTranslation);
		renderer.repaint();
	}


	/**
	 * Re-initializes the renderer.
	 * This is used when reloading the editor, or opening a map file. It resets 
	 * the view position, zoom level, selected indices, and any other dependent 
	 * variables.
	 */
	private void reset() {
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load editor config file from resources");
			e.printStackTrace();
			exit();
			return;

		}
		selSecIndex		= -1;
		selCorIndex		= -1;
		mapTranslation			= new PVec2(0.0, 0.0);
		dragStartMapTranslation	= new PVec2(0.0, 0.0);
		dragStartX				= 0;
		dragStartY				= 0;
		history.clear();
		redoHistory.clear();
	}

	/**
	 * Returns a list of data panel entries for updating corner data while it 
	 * is being dragged.
	 * @param  x the world space x coordinate of the corner
	 * @param  y the world space y coordinate of the corner
	 * @return   a list of data panel entries representing all the data about 
	 * 			 the selected sector, updated live with the current position of 
	 * 			 the moving corner.
	 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry
	 */
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

	/**
	 * Returns a list of data panel entries for the selected sector and corner.
	 * @return   a list of data panel entries representing all the data about 
	 * 			 the selected sector
	 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry
	 */
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

	/**
	 * Checks if a corner is present at given screen space coordinates.
	 * @param  x screen space x coordinate to search for a corner at
	 * @param  y screen space y coordinate to search for a corner at
	 * @return   {@code true} if there is a corner at the given coordinates,
	 * 			 else {@code false}
	 */
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
					selSecIndex = i;
					selCorIndex = j;
					return true;

				}
			}
		}
		selSecIndex = -1;
		selCorIndex = -1;
		return false;

	}
}
