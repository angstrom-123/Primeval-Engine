package com.ang.peEditor;

public interface PEditorInterface {
	void save(String name);
	void open(String name);
	void newFile();
	void exit();
	void undo();
	void redo();
	void newSector();
	void newCorner();
}
