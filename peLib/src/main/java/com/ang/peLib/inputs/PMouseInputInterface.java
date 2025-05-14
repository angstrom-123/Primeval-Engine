package com.ang.peLib.inputs;

/**
 * Interface for mouse events.
 */
public interface PMouseInputInterface {
	/**
	 * Handles the mouse scroll event.
	 * @param units the amount that the mouse was scrolled
	 */
	void mouseScrolled(int units);

	/**
	 * Handles the mouse move event.
	 * @param x the x screen space coordinate of the mouse
	 * @param y the y screen space coordinate of the mouse
	 */
	void mouseMoved(int x, int y);

	/**
	 * Handles the mouse drag event.
	 * @param x the x screen space coordinate of the mouse
	 * @param y the y screen space coordinate of the mouse
	 */
	void mouseDragged(int x, int y);

	/**
	 * Handles the mouse press event.
	 * @param x the x screen space coordinate of the mouse
	 * @param y the y screen space coordinate of the mouse
	 */
	void mousePressed(int x, int y);

	/**
	 * Handles the right mouse press event.
	 * @param x the x screen space coordinate of the mouse
	 * @param y the y screen space coordinate of the mouse
	 */
	void rightMousePressed(int x, int y);

	/**
	 * Handles the mouse release event.
	 * @param x the x screen space coordinate of the mouse
	 * @param y the y screen space coordinate of the mouse
	 */
	void mouseReleased(int x, int y);

	/**
	 * Handles the mouse exit event.
	 */
	void mouseExited();
}
