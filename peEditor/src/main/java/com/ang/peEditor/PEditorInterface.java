package com.ang.peEditor;

import com.ang.peEditor.dataPanel.PDataPanelEntry;

public interface PEditorInterface {
	void dataPanelChange(PDataPanelEntry entry, String text);
	void save(String name);
	void open(String name);
	void newFile();
	void exit();
	void undo();
	void redo();
	void newSector(int cornerCount);
}
