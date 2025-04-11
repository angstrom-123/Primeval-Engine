package com.ang.peLib.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PMovementInputListener implements KeyListener {
	private PMovementInputInterface movementInterface;

	public PMovementInputListener(PMovementInputInterface movementInterface) {
		this.movementInterface = movementInterface;	
	}

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

	@Override
	public void keyTyped(KeyEvent e) {}
}
