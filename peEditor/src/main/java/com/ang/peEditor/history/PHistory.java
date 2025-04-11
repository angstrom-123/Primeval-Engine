package com.ang.peEditor.history;

public class PHistory {
	private int head = 0;
	private int tail = 0;
	private PHistoryEntry[] history;

	public PHistory(int historyLength) {
		history = new PHistoryEntry[historyLength];
	}

	public void push(PHistoryEntryType entryType, double[] values) {
		boolean isLong;
		if (values.length == 4) {
			isLong = true;
		} else if (values.length == 2) {
			isLong = false;
		} else {
			System.err.println("Invalid args length in PHistory:push");
			return; 

		}
		PHistoryEntry entry = new PHistoryEntry();
		entry.entryType = entryType;
		entry.setData(isLong, values);
		push(entry);
	}

	public void push(PHistoryEntry entry) {
		int newTail = tail;
		newTail = (tail == history.length - 1) ? 0 : tail + 1;
		int newHead = head;
		if (newTail == head) {
			newHead = (head == history.length - 1) ? 0 : head + 1;
		}
		tail = newTail;
		head = newHead;
		history[tail] = entry;
	}

	public PHistoryEntry pop() {
		PHistoryEntry out = history[head].copy();
		history[head] = null;
		head = (head == history.length - 1) ? 0 : head + 1;
		return out;

	}
}
