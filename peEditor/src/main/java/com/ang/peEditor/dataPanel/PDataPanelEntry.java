package com.ang.peEditor.dataPanel;

public class PDataPanelEntry {
	public final static int TOP = 0;
	public final static int BOT = 1;

	public int entryIndex;
	public int panelIndex;
	public String heading;
	public String data;
	public boolean readOnly = false;

	public PDataPanelEntry(int entryIndex, int panelIndex, String heading, String data, boolean readOnly) {
		this.entryIndex = entryIndex;
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = data;
		this.readOnly = readOnly;
	}

	public PDataPanelEntry(int entryIndex, int panelIndex, String heading, int data, boolean readOnly) {
		this.entryIndex = entryIndex;
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
		this.readOnly = readOnly;
	}

	public PDataPanelEntry(int entryIndex, int panelIndex, String heading, double data, boolean readOnly) {
		this.entryIndex = entryIndex;
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
		this.readOnly = readOnly;
	}

	public PDataPanelEntry(int entryIndex, int panelIndex, String heading, boolean data, boolean readOnly) {
		this.entryIndex = entryIndex;
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
		this.readOnly = readOnly;
	}
}
