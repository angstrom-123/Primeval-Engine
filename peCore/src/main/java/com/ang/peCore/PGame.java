package com.ang.peCore;

import com.ang.peCore.threads.*;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.*;
import com.ang.peLib.files.pmap.*;

public class PGame implements PThreadInterface, PMovementInputInterface {
	private final int IMAGE_WIDTH = 600;
	private final int FRAME_MS = 1000 / 60;
	private boolean[] keyInputs = new boolean[256];
	private PMovementInputListener listener = new PMovementInputListener(this);
	private PCamera cam = new PCamera(IMAGE_WIDTH);
	private PCameraMover controller = new PCameraMover(cam);
	private PSectorWorld world;

	public void start(boolean test) {
		if (test) {
			testGame();
		} else {
			runGame();
		}
	}

	// TODO:
	private void runGame() {
		System.out.println("Regular game entry point goes here");
	}

	private void testGame() {
		if (!loadMapFile("testMap.pmap")) {
			System.err.println("Failed to load test level");
			return;

		}
		cam.init(listener);
		PGlobal.uw = new PUpdateWorker(FRAME_MS);
		PGlobal.uw.setInterface(this);
		PGlobal.uw.run();
	}

	private boolean loadMapFile(String fileName) {
		PPMapHandler handler = new PPMapHandler();
		try {
			handler.loadMapData(fileName);
		} catch (PResourceException e) {
			e.printStackTrace();
			return false;

		}
		PPMapData mapData = handler.getSaveData().savedMapData;
		world = mapData.world;
		cam.setTransform(mapData.position, mapData.facing);
		return true;

	}

	@Override
	public void update() {
		controller.update(keyInputs);
		cam.update();
		cam.draw(world);
	}

	@Override
	public void pressed(int key) {
		keyInputs[key] = true;
	}

	@Override
	public void released(int key) {
		keyInputs[key] = false;
	}

}
