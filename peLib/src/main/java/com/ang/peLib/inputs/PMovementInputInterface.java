package com.ang.peLib.inputs;

/**
 * Interface for keyboard events.
 */
public interface PMovementInputInterface {
	/**
	 * Handles the key pressed event.
	 * @param key the VK keycode of the key pressed
	 */
	void pressed(int key);

	/**
	 * Handles the key released event.
	 * @param key the VK keycode of the key released
	 */
	void released(int key);
}
