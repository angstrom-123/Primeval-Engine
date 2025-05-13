package com.ang.peEditor.dataPanel;

public interface PDataChangeListener {
	void dataChange(int panelIndex, int entryIndex, int value);
	void dataChange(int panelIndex, int entryIndex, double value);
	void dataChange(int panelIndex, int entryIndex, boolean value);
}
