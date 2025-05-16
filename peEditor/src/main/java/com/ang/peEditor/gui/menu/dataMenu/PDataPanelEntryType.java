package com.ang.peEditor.gui.menu.dataMenu;

public enum PDataPanelEntryType {
	CORNER_INDEX("Index", true),
	CORNER_X("x"),
	CORNER_Y("y"),
	CORNER_IS_PORTAL("Portal"),
	SECTOR_INDEX("Index", true),
	SECTOR_FLOOR("Floor Height"),
	SECTOR_CEILING("Ceiling Height"),
	SECTOR_LIGHT("Light Level");

	private String heading;
	private boolean readOnly;

	private PDataPanelEntryType(String heading, boolean readOnly) {
		this.heading = heading;
		this.readOnly = readOnly;
	}

	private PDataPanelEntryType(String heading) {
		this(heading, false);

	}

	public String getHeading() {
		return heading;

	}

	public boolean editable() {
		return readOnly;

	}
}
