package com.ang.peEditor.history;

public class PHistoryEntry {
	public PHistoryEntryType entryType;
	public double before0;
	public double after0;
	public double before1;
	public double after1;

	public PHistoryEntry() {}

	public PHistoryEntry(PHistoryEntryType entryType) {
		this.entryType = entryType;
	}

	public PHistoryEntry copy() {
		PHistoryEntry temp = new PHistoryEntry();
		temp.entryType = entryType;
		temp.setData(true, new double[]{before0, after0, before1, after1});
		return temp;

	}

	public void setData(boolean isLong, double[] data) {
		before0= data[0];
		after0= data[1];
		if (isLong) {
			before1 = data[2];
			after1 = data[3];
		}
	}
}
