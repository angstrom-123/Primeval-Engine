package com.ang.peEditor.gui.menu.rmbMenu;

/**
 * Interface for handling right click menu events.
 * @see PDataPanel
 * @see PDataPanelEntry
 */
public interface PRMBPanelListener {
	void rmbActionPerformed(int cornerIndex, int sectorIndex, String action);
	void rmbMouseExit();
}
