package com.ang.peEditor.gui.menu.dataMenu;

/**
 * Interfae for data panel change events.
 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanel
 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry
 */
public interface PDataChangeListener {
	void dataChange(PDataPanelEntry entry, String text);
}
