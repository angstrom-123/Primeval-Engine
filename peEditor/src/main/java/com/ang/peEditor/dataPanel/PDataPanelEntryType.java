package com.ang.peEditor.dataPanel;

public enum PDataPanelEntryType {
	CORNER_INDEX("Index"),
	CORNER_X("x"),
	CORNER_Y("y"),
	CORNER_IS_PORTAL("Portal"),
	SECTOR_INDEX("Index"),
	SECTOR_FLOOR("Floor Height"),
	SECTOR_CEILING("Ceiling Height"),
	SECTOR_LIGHT("Light Level");

	private String heading;

	private PDataPanelEntryType(String heading) {
		this.heading = heading;
	}

	public String getHeading() {
		return heading;

	}
}
