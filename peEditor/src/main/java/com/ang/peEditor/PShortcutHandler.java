package com.ang.peEditor;

import java.awt.event.KeyEvent;
import com.ang.peLib.inputs.PFullKeyboardInputInterface;

/**
 * Implements the full keyboard interface to detect certain keyboard shorcuts.
 */
public class PShortcutHandler implements PFullKeyboardInputInterface {
	private boolean ctrlHeld = false;
	private boolean shiftHeld = false;
	private PEditorInterface ei;

	/**
	 * Constructs a new shortcut handler with an interface to send events to.
	 * @param ei editor interface to receive events from this handler
	 */
	public PShortcutHandler(PEditorInterface ei) {
		this.ei = ei;
	}

	/**
	 * Handles the key pressed event.
	 * Checks if a modifier is being pressed and sends the correct event to 
	 * the interface
	 * @param key the key that was pressed
	 */
	@Override
	public void pressed(int key) {
		if (key == KeyEvent.VK_CONTROL) ctrlHeld = true;
		if (key == KeyEvent.VK_SHIFT) shiftHeld = true;
		if (ctrlHeld && (key == KeyEvent.VK_Z)) {
			if (shiftHeld) {
				ei.redo();
			} else {
				ei.undo();
			}
		}
	}

	/**
	 * Handles the key released event.
	 * Updates the state of modifier keys (held / not held)
	 * @param key the key that was released
	 */
	@Override
	public void released(int key) {
		if (key == KeyEvent.VK_CONTROL) ctrlHeld = false;
		if (key == KeyEvent.VK_SHIFT) shiftHeld = false;
	}
}
