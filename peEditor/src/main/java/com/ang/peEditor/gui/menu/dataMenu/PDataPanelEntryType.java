package com.ang.peEditor.gui.menu.dataMenu;

/**
 * Specifies the different types of entry for a data panel
 */
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

	/**
	 * Constructs a new entry type with a heading and a flag to set if it should 
	 * not be editable.
	 * @param heading  the heading for the entry type 
	 * @param readOnly if the entry should be read only (not editable)
	 */
	private PDataPanelEntryType(String heading, boolean readOnly) {
		this.heading = heading;
		this.readOnly = readOnly;
	}

	/**
	 * Constructs a new entry type with a heading.
	 * @param heading  the heading for the entry type 
	 */
	private PDataPanelEntryType(String heading) {
		this(heading, false);

	}

	/**
	 * Returns the heading for the entry.
	 * @return the heading for the entry
	 */
	public String getHeading() {
		return heading;

	}

	/**
	 * Returns if the entry is editable.
	 * @return {@code true} if the entry is editable, else {@code false}
	 */
	public boolean editable() {
		return readOnly;

	}
}
