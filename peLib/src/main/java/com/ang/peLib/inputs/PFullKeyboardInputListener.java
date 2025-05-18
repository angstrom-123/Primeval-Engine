package com.ang.peLib.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Class for registering keyboard shortcut inputs.
 * All events are passed to the interface {@link PMovementInputInterface}
 * @see PMovementInputInterface
 */
public class PFullKeyboardInputListener extends PListener implements KeyListener {
	private PFullKeyboardInputInterface keyboardInterface;

	/**
	 * Constructs the listener with an output interface.
	 * @param movementInterface the {@link PMovementInputInterface} to send events to
	 * @see 				    PMovementInputInterface
	 */
	public PFullKeyboardInputListener(PFullKeyboardInputInterface keyboardInterface) {
		this.keyboardInterface = keyboardInterface;	
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
		keyboardInterface.pressed(key);
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
		keyboardInterface.released(key);
	}

	/**
	 * {@inheritDoc}
	 * Currently not implemented in the listener.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
}
