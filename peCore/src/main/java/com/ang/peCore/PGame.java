package com.ang.peCore;

import com.ang.peLib.threads.*;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.*;
import com.ang.peLib.files.pmap.*;

public class PGame implements PThreadInterface, PMovementInputInterface {
	private final int horizontalResolution = PGameParams.horizontalResolution;
	private final int frameMs = 1000 / PGameParams.frameRate;
	private boolean[] keyInputs = new boolean[256];
	private PMovementInputListener listener = new PMovementInputListener(this);
	private PCamera cam = new PCamera(horizontalResolution);
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
		// if (!loadMapFile("testMap.pmap")) {
		if (!loadMapFile("george.pmap")) {
			System.err.println("Failed to load test level");
			return;

		}
		cam.init(listener);
		PUpdateWorker uw = new PUpdateWorker(frameMs);
		uw.setInterface(this);
		cam.getRenderer().terminateOnClose(uw);
		uw.run();
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
