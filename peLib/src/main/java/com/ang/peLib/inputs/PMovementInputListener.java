package com.ang.peLib.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Class for registering keyboard inputs.
 * All events are passed to the interface {@link PMovementInputInterface}
 * @see PMovementInputInterface
 */
public class PMovementInputListener implements KeyListener {
	private PMovementInputInterface movementInterface;

	/**
	 * Constructs the listener with an output interface.
	 * @param movementInterface the {@link PMovementInputInterface} to send events to
	 * @see 				    PMovementInputInterface
	 */
	public PMovementInputListener(PMovementInputInterface movementInterface) {
		this.movementInterface = movementInterface;	
	}

	/**
	 * {@inheritDoc}
	 * Sends the key pressed event to the {@link PMovementInputInterface#pressed(int)} 
	 * as specified in the constructor (only listens for specific keys).
	 * @see PMovementInputInterface#pressed(int)
	 * @see #PMovementInputListener(PMovementInputInterface)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W) {
			movementInterface.pressed(key);
		}
		if (key == KeyEvent.VK_A) {
			movementInterface.pressed(key);
		}
		if (key == KeyEvent.VK_S) {
			movementInterface.pressed(key);
		}
		if (key == KeyEvent.VK_D) {
			movementInterface.pressed(key);
		}
		if (key == KeyEvent.VK_LEFT) {
			movementInterface.pressed(key);
		}
		if (key == KeyEvent.VK_RIGHT) {
			movementInterface.pressed(key);
		}
	}

	/**
	 * {@inheritDoc}
	 * Sends the key released event to the {@link PMovementInputInterface#released(int)} 
	 * as specified in the constructor (only listens for specific keys).
	 * @see PMovementInputInterface#released(int)
	 * @see #PMovementInputListener(PMovementInputInterface)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W) {
			movementInterface.released(key);
		}
		if (key == KeyEvent.VK_A) {
			movementInterface.released(key);
		}
		if (key == KeyEvent.VK_S) {
			movementInterface.released(key);
		}
		if (key == KeyEvent.VK_D) {
			movementInterface.released(key);
		}
		if (key == KeyEvent.VK_LEFT) {
			movementInterface.released(key);
		}
		if (key == KeyEvent.VK_RIGHT) {
			movementInterface.released(key);
		}
	}

	/**
	 * {@inheritDoc}
	 * Currently not implemented in the listener.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
}
