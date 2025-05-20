package com.ang.peCore;

import com.ang.peLib.maths.PVec2;

import java.awt.event.KeyEvent;

public class PCameraMover {
	private final double	MOVEMENT_STEP 	= 0.18;
	private final double	TURN_STEP 		= Math.PI / 45.0;
	private PCamera 		camera;

	public PCameraMover(PCamera camera) {
		this.camera = camera;	
	}

	public void update(boolean[] inputs) {
		camera.changePosition(getMovementInput(inputs));
		camera.changeFacing(getTurnInput(inputs));
	}

	private double getTurnInput(boolean[] inputs) {
		double theta = 0.0;
		if (inputs[KeyEvent.VK_LEFT]) { 
			theta += TURN_STEP;
		}
		if (inputs[KeyEvent.VK_RIGHT]) { 
			theta -= TURN_STEP;
		}
		return theta;

	}

	private PVec2 getMovementInput(boolean[] inputs) {
		PVec2 out = new PVec2(0.0, 0.0);
		if (inputs[KeyEvent.VK_W]) {
			out = out.add(new PVec2(0.0, MOVEMENT_STEP));
		}
		if (inputs[KeyEvent.VK_A]) {
			out = out.add(new PVec2(MOVEMENT_STEP, 0.0));
		}
		if (inputs[KeyEvent.VK_S]) {
			out = out.add(new PVec2(0.0, -MOVEMENT_STEP));
		}
		if (inputs[KeyEvent.VK_D]) {
			out = out.add(new PVec2(-MOVEMENT_STEP, 0.0));
		}
		return out;

	}
}
