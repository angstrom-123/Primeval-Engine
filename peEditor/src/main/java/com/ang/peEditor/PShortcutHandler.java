package com.ang.peEditor;

import java.awt.event.KeyEvent;
import com.ang.peLib.inputs.PFullKeyboardInputInterface;

public class PShortcutHandler implements PFullKeyboardInputInterface {
	private boolean ctrlHeld = false;
	private boolean shiftHeld = false;
	private PEditorInterface ei;

	public PShortcutHandler(PEditorInterface ei) {
		this.ei = ei;
	}

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

	@Override
	public void released(int key) {
		if (key == KeyEvent.VK_CONTROL) ctrlHeld = false;
		if (key == KeyEvent.VK_SHIFT) shiftHeld = false;
	}
}
