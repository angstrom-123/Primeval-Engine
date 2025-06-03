package com.ang.peLib.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Class for registering keyboard inputs.
 * All events are passed to the interface {@link PMovementInputInterface}
 * @see PMovementInputInterface
 */
public class PMovementInputListener extends PListener implements KeyListener {
	private PMovementInputInterface movementInterface;
	private final int[] TRACKED = new int[]{
			KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, 
			KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN};

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
		for (int i : TRACKED) if (i == key) movementInterface.pressed(key);
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
		for (int i : TRACKED) if (i == key) movementInterface.released(key);
	}

	/**
	 * {@inheritDoc}
	 * Currently not implemented in the listener.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
}
