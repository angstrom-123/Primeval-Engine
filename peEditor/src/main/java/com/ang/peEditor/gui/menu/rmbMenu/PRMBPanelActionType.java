package com.ang.peEditor.gui.menu.rmbMenu;

/**
 * Specifies different action types for the right click menu.
 * @see PRMBPanel
 */
public enum PRMBPanelActionType {
	DELETE_SECTOR("Delete Sector"),
	DELETE_CORNER("Delete Corner"),
	ADD_CORNER_LEFT("Insert Before"),
	ADD_CORNER_RIGHT("Insert After");

	private String action;

	/**
	 * Initializes an action type with an action string.
	 * @param action the action that this action type represents
	 */
	private PRMBPanelActionType(String action) {
		this.action = action;
	}

	/**
	 * Returns the action for this action type.
	 * @return the action for this type
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Returns all possible actions.
	 * @return array of all actions
	 */
	public static String[] getActions() {
		String[] out = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			out[i] = values()[i].getAction();	
		}
		return out;

	}
}
