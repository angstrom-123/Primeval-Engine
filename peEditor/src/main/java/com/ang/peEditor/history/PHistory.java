package com.ang.peEditor.history;

import com.ang.peLib.hittables.PSectorWorld;

/**
 * Saves world states in a stack for undoing / redoing.
 * @see com.ang.peLib.hittables.PSectorWorld
 */
public class PHistory {
	private PSectorWorld[] entries;

	/**
	 * Constructs a new editor history.
	 * @param length the length of history to save
	 */
	public PHistory(int length) {
		this.entries = new PSectorWorld[length];
	}

	/**
	 * Pushes a new entry to the history.
	 * @param entry the entry to insert
	 * @see   		com.ang.peLib.hittables.PSectorWorld
	 */
	public void push(PSectorWorld entry) {
		PSectorWorld[] newEntries = new PSectorWorld[entries.length];
		for (int i = 0; i < entries.length - 1; i++) {
			if (entries[i] == null) break;

			newEntries[i + 1] = entries[i].copy();
		}
		newEntries[0] = entry.copy();
		entries = newEntries;
	}

	/**
	 * Removes and returns the most recent history entry.
	 * @return the most recently added entry
	 * @see    com.ang.peLib.hittables.PSectorWorld
	 */
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

	/**
	 * Clears the saved entries in the history.
	 */
	public void clear() {
		entries = new PSectorWorld[entries.length];
	}
}
