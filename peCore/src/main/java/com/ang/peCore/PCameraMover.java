package com.ang.peCore;

import com.ang.peLib.maths.PVec2;

import java.awt.event.KeyEvent;

/**
 * Provides an interface for controlling the camera's position and facing.
 */
public class PCameraMover {
	private final double MOVEMENT_STEP = 0.18;
	private final double TURN_STEP = Math.PI / 45.0;
	private final double ELEVATION_STEP = 0.18;
	private PCamera camera;

	/**
	 * Constructs a new camera mover with a camera to move.
	 * @param camera the camera to control 
	 * @see 		 PCamera
	 */
	public PCameraMover(PCamera camera) {
		this.camera = camera;	
	}

	/**
	 * Updates the camera's position and rotation based on the current inputs.
	 * Input array is given such that the keycode of each key is an index into 
	 * the array and the boolean value represents if it is held or not.
	 * @param inputs the currently held keys
	 */
	public void update(boolean[] inputs) {
		camera.changePosition(getMovementInput(inputs));
		camera.changeFacing(getTurnInput(inputs));
		camera.changeElevation(getElevationInput(inputs));
	}

	/**
	 * Gets the current camera rotation input from the inputs.
	 * Input array is given such that the keycode of each key is an index into 
	 * the array and the boolean value represents if it is held or not.
	 * @param  inputs the currently held keys
	 * @return 		  angle in radians to turn
	 */
	private double getTurnInput(boolean[] inputs) {
		double theta = 0.0;
		if (inputs[KeyEvent.VK_LEFT]) theta += TURN_STEP;
		if (inputs[KeyEvent.VK_RIGHT]) theta -= TURN_STEP;
		return theta;

	}

	/**
	 * Gets the current camera lateral movement input from the inputs.
	 * Input array is given such that the keycode of each key is an index into 
	 * the array and the boolean value represents if it is held or not.
	 * @param  inputs the currently held keys
	 * @return 		  vector to move the camera in
	 * @see 		  com.ang.peLib.maths.PVec2
	 */
	private PVec2 getMovementInput(boolean[] inputs) {
		PVec2 out = new PVec2(0.0, 0.0);
		if (inputs[KeyEvent.VK_W]) out = out.add(new PVec2(0.0, MOVEMENT_STEP));
		if (inputs[KeyEvent.VK_A]) out = out.add(new PVec2(MOVEMENT_STEP, 0.0));
		if (inputs[KeyEvent.VK_S]) out = out.add(new PVec2(0.0, -MOVEMENT_STEP));
		if (inputs[KeyEvent.VK_D]) out = out.add(new PVec2(-MOVEMENT_STEP, 0.0));
		return out;

	}

	/**
	 * Gets the current camera elevation movement input from the inputs.
	 * Input array is given such that the keycode of each key is an index into 
	 * the array and the boolean value represents if it is held or not.
	 * @param  inputs the currently held keys
	 * @return 		  units to move the camera up or down
	 */
	private double getElevationInput(boolean[] inputs) {
		double delta = 0.0;
		if (inputs[KeyEvent.VK_UP]) delta += ELEVATION_STEP;
		if (inputs[KeyEvent.VK_DOWN]) delta -= ELEVATION_STEP;
		return delta;

	}
}
