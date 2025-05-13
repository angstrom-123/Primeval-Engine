package com.ang.peEditor.dataPanel;

public class PDataPanelEntry {
	public final static int TOP = 0;
	public final static int BOT = 1;

	public PDataPanelEntryType entryType; 
	public int panelIndex;
	public String heading;
	public String data;
	public int sectorIndex;
	public int cornerIndex;
	public boolean readOnly = false;
	public boolean booleanOnly = false;

	public PDataPanelEntry(PDataPanelEntryType entryType, int panelIndex, 
			String data, int sectorIndex, int cornerIndex) {
		this.entryType = entryType;
		this.panelIndex = panelIndex;
		this.heading = entryType.getHeading();
		this.data = data;
		this.sectorIndex = sectorIndex;
		this.cornerIndex = cornerIndex;
		this.readOnly = entryType.editable();
	}

	public PDataPanelEntry(PDataPanelEntryType entryType, int panelIndex, 
			int data, int sectorIndex, int cornerIndex) {
		this(entryType, panelIndex, String.valueOf(data), sectorIndex, cornerIndex);
	}

	public PDataPanelEntry(PDataPanelEntryType entryType, int panelIndex, 
			double data, int sectorIndex, int cornerIndex) {
		this(entryType, panelIndex, String.valueOf(data), sectorIndex, cornerIndex);
	}

	public PDataPanelEntry(PDataPanelEntryType entryType, int panelIndex, 
			boolean data, int sectorIndex, int cornerIndex) {
		this(entryType, panelIndex, String.valueOf(data), sectorIndex, cornerIndex);
		booleanOnly = true;
	}
}
