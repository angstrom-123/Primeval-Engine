package com.ang.peEditor.history;

import com.ang.peLib.utils.PCopyable;

public class PHistoryEntry<T, U> extends PCopyable {
	public PHistoryEntryType entryType;
	public Object field0;
	public Object field1;

	public PHistoryEntry(PHistoryEntryType entryType) {
		this.entryType = entryType;
	}

	public void setFields(Object value0, Object value1) {
		this.field0 = value0;
		this.field1 = value1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PHistoryEntry<T, U> copy() {
		return new PHistoryEntry<T, U>(this.entryType);

	}
}
