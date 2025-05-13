package com.ang.peEditor.dataPanel;

public enum PDataPanelEntryType {
	CORNER_INDEX("Index"),
	CORNER_X("x", true),
	CORNER_Y("y", true),
	CORNER_IS_PORTAL("Portal"),
	SECTOR_INDEX("Index"),
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
