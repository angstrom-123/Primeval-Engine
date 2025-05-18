package com.ang.peEditor.history;

import com.ang.peLib.hittables.PSectorWorld;

public class PHistory { // circular stack
	private PSectorWorld[] entries;
	private int head = 0;

	public PHistory(int length) {
		this.entries = new PSectorWorld[length];
	}

	public void push(PSectorWorld entry) {
		entries[head++] = entry;
		if (head == entries.length) {
			head = 0;
		}
	}

	public PSectorWorld pop() {
		PSectorWorld out = null;
		if (head != 0) {
			out = entries[--head].copy();
			entries[head] = null;

		}
		return out;

	}

	public int getSize() {
		return head;

	}

	public void clear() {
		entries = new PSectorWorld[entries.length];
	}
}
