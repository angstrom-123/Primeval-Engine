package com.ang.peEditor.history;

import com.ang.peLib.hittables.PSectorWorld;

public class PHistory { // circular stack
	private PSectorWorld[] entries;

	public PHistory(int length) {
		this.entries = new PSectorWorld[length];
	}

	public void push(PSectorWorld entry) {
		PSectorWorld[] newEntries = new PSectorWorld[entries.length];
		for (int i = 0; i < entries.length - 1; i++) {
			if (entries[i] == null) break;

			newEntries[i + 1] = entries[i].copy();
		}
		newEntries[0] = entry.copy();
		entries = newEntries;
	}

	public PSectorWorld pop() {
		PSectorWorld out = entries[0];
		PSectorWorld[] newEntries = new PSectorWorld[entries.length];
		for (int i = 0; i < entries.length - 1; i++) {
			if (entries[i + 1] == null) break;

			newEntries[i] = entries[i + 1].copy();
		}
		entries = newEntries;
		return out;

	}

	public void clear() {
		entries = new PSectorWorld[entries.length];
	}
}
