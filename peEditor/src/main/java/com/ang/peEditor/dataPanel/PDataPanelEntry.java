package com.ang.peEditor.dataPanel;

public class PDataPanelEntry {
	public final static int TOP = 0;
	public final static int BOT = 1;

	public int panelIndex;
	public String heading;
	public String data;

	public PDataPanelEntry(int panelIndex, String heading, String data) {
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = data;
	}

	public PDataPanelEntry(int panelIndex, String heading, int data) {
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
	}

	public PDataPanelEntry(int panelIndex, String heading, double data) {
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
	}

	public PDataPanelEntry(int panelIndex, String heading, boolean data) {
		this.panelIndex = panelIndex;
		this.heading = heading;
		this.data = String.valueOf(data);
	}
}
