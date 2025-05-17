package com.ang.peEditor.history;

public class PHistory {
	private int head = 0;
	private int tail = 0;
	private PHistoryEntry<?, ?>[] history;

	public PHistory(int historyLength) {
		history = new PHistoryEntry[historyLength];
	}

	public void printHistory() {
		for (int i = 0; i < history.length; i++) {
			if (history[i] == null) continue;

			String heading;
			if (i == head) {
				heading = "head";
			} else if (i == tail) {
				heading = "tail";
			} else {
				heading = String.valueOf(i);
			}
			PHistoryEntry<?, ?> e = history[i];
			System.out.println(heading + " | " + e.field0 + " " + e.field1);
		}
	}

	public void push(PHistoryEntry<?, ?> entry) {
		int newTail = tail;
		newTail = (tail == history.length - 1) ? 0 : tail + 1;
		int newHead = head;
		if (newTail == head) {
			newHead = (head == history.length - 1) ? 0 : head + 1;
		}
		tail = newTail;
		head = newHead;
		history[tail] = entry;
		printHistory();
	}

	public PHistoryEntry<?, ?> pop() {
		PHistoryEntry<?, ?> out = history[head].copy();
		history[head] = null;
		head = (head == history.length - 1) ? 0 : head + 1;
		return out;

	}
}
