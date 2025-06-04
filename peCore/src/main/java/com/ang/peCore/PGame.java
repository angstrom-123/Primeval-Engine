package com.ang.peCore;

import java.awt.event.KeyEvent;

import com.ang.peLib.utils.PConvexDecomposer;
import com.ang.peLib.threads.*;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.*;
import com.ang.peLib.files.pmap.*;

/**
 * Main class for the core game.
 */
public class PGame implements PThreadInterface, PMovementInputInterface, PFullKeyboardInputInterface {
	private long debounceTimer = 0;
	private boolean[] keyInputs = new boolean[256];
	private PFullKeyboardInputListener listener = new PFullKeyboardInputListener(this);
	private int frameMs;
	private PCamera cam;
	private PCameraMover controller;
	private PSectorWorld world;
	private PGameParams params;

	/**
	 * Constructs a new game.
	 * Initializes the game
	 */
	public PGame() {
		init();
	}

	/**
	 * Initializes the game.
	 * Attempts to load params from the config file in resources, initializes 
	 * camera and camera controller.
	 */
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

	/**
	 * Runs the game.
	 * @param test specifies if the game should be run in test mode
	 */
	public void start(boolean test) {
		if (test) {
			testGame();
		} else {
			runGame();
		}
	}

	/**
	 * Runs the game.
	 * This is the regular entry point for the game.
	 */
	private void runGame() {
		// TODO
		System.out.println("Regular game entry point goes here, try running with "
				+ "the -t flag for test mode");
	}

	/**
	 * Tests the game.
	 * This is the entry point for the game test.
	 * Loads a map file from resources an starts up the update worker.
	 */
	private void testGame() {
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

	/**
	 * Loads a map file from resources.
	 * @param fileName name of the map file to load (including file extension)
	 */
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

	/**
	 * Update loop.
	 * Called by the update worker
	 */
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

	/**
	 * Key press event handler.
	 * @param key keycode of the key that was pressed
	 */
	@Override
	public void pressed(int key) {
		keyInputs[key] = true;
	}

	/**
	 * Key release event handler.
	 * @param key keycode of the key that was released
	 */
	@Override
	public void released(int key) {
		keyInputs[key] = false;
	}

}
