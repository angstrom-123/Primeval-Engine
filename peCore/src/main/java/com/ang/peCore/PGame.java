package com.ang.peCore;

import java.awt.event.KeyEvent;

import com.ang.peLib.utils.PConvexDecomposer;
import com.ang.peLib.threads.*;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.*;
import com.ang.peLib.files.pmap.*;

public class PGame implements PThreadInterface, PMovementInputInterface, PFullKeyboardInputInterface {
	private long debounceTimer = 0;
	private boolean[] keyInputs = new boolean[256];
	private PFullKeyboardInputListener listener = new PFullKeyboardInputListener(this);
	private int frameMs;
	private PCamera cam;
	private PCameraMover controller;
	private PSectorWorld world;
	private PGameParams params;

	public PGame() {
		init();
	}

	private void init() {
		params = new PGameParams();
		try {
			params.init();
		} catch (PResourceException e) {
			System.err.println("Failed to load game config file from resources");
			e.printStackTrace();
			return;

		}
		frameMs = 1000 / params.frameRate;
		cam = new PCamera(params);
		controller = new PCameraMover(cam); 
	}

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
		// String testFile = "simpleTest.pmap";
		String testFile = "test.pmap";
		if (loadMapFile(testFile)) {
			PUpdateWorker uw = new PUpdateWorker(frameMs);
			uw.setInterface(this);
			cam.getRenderer().terminateOnClose(uw);
			uw.run();
		} else {
			System.out.println("Failed to load test level");
		}
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
		PConvexDecomposer decomposer = new PConvexDecomposer(world);
		world = decomposer.decompose();
		cam.setTransform(mapData.position, mapData.facing);
		cam.init(listener);
		return true;

	}

	@Override
	public void update() {
		final long debounceTime = 200;
		if ((System.currentTimeMillis() - debounceTimer > debounceTime) 
				&& (keyInputs[KeyEvent.VK_CONTROL] && keyInputs[KeyEvent.VK_M])) {
			cam.cycleRenderMode();
			debounceTimer = System.currentTimeMillis();
		}
		controller.update(keyInputs);
		cam.update();
		long renderMS = cam.draw(world);
		cam.getRenderer().writeToTitleBar("Frame ms: " + String.valueOf(renderMS));
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
