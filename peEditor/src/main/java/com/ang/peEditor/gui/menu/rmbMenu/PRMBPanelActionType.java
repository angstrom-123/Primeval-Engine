package com.ang.peEditor.gui.menu.rmbMenu;

public enum PRMBPanelActionType {
	DELETE_SECTOR("Delete Sector"),
	DELETE_CORNER("Delete Corner"),
	ADD_CORNER_LEFT("Insert Before"),
	ADD_CORNER_RIGHT("Insert After");

	private String action;

	private PRMBPanelActionType(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public static String[] getActions() {
		String[] out = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			out[i] = values()[i].getAction();	
		}
		return out;

	}
}
