package com.ang.peEditor;

import com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry;

/**
 * Interface for sending events to the main editor.
 */
public interface PEditorInterface {
	void changePosition(double x, double y);
	void changeFacing(double x, double y);
	void dataPanelChange(PDataPanelEntry entry, String text);
	void save(String name);
	void open(String name);
	void newFile();
	void exit();
	void undo();
	void redo();
	void newSector(int cornerCount, double scale);
	void delSector(int sectorIndex);
	void delCorner(int cornerIndex, int sectorIndex);
	void insCornerLeft(int cornerIndex, int sectorIndex);
	void insCornerRight(int cornerIndex, int sectorIndex);
}
