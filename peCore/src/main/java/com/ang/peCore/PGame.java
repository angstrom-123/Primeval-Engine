package com.ang.peCore;

import com.ang.peCore.threads.*;
import com.ang.peLib.resources.*;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.*;
import com.ang.peLib.files.PFileReader;
import com.ang.peLib.files.pmap.*;

public class PGame implements PThreadInterface, PMovementInputInterface {
	private final String MAP_DIR_PATH = "/mapData/";
	private final int IMAGE_WIDTH = 600;
	private final int FRAME_MS = 1000 / 60;
	private boolean[] keyInputs = new boolean[256];
	private PMovementInputListener listener = new PMovementInputListener(this);
	private PCamera cam = new PCamera(IMAGE_WIDTH);
	private PCameraMover controller = new PCameraMover(cam);
	private PSectorWorld world;

	public PGame() {}

	public void start(PMode[] modes) {
		boolean test = false;
		boolean edit = false;
		boolean game = false;
		for (PMode mode : modes) {
			switch (mode) {
			case TEST:
				test = true;
				break;
			
			case EDIT:
				edit = true;
				break;

			case GAME:
				game = true;
				break;

			}
		}
		if ((edit && game) || (test && !edit && !game)) {
			System.err.println("Invalid arguments");
			return;

		}
		if (game) {
			if (test) {
				testGame();
			} else {
				runGame();
			}
		} else if (edit) {
			if (test) {
				testEditor();
			} else {
				runEditor();
			}
		}
	}

	// TODO:
	private void runEditor() {
		System.out.println("Regular editor entry point goes here");
	}

	// TODO:
	private void runGame() {
		System.out.println("Regular game entry point goes here");
	}

	private void testEditor() {
		System.out.println("editor test");
	}

	private void testGame() {
		if (!loadMapFile("testMap.pmap")) {
			System.err.println("Failed to load test level");
			return;

		}
		System.out.println("Test level loaded successfully");
		cam.init(listener);
		PGlobal.uw = new PUpdateWorker(FRAME_MS);
		PGlobal.uw.setInterface(this);
		PGlobal.uw.run();
	}

	private boolean loadMapFile(String fileName) {
		PFileReader mapReader = new PFileReader();
		String[] fileData;
		try {
			fileData = mapReader.readFile(PResourceType.PMAP, fileName);
		} catch (PResourceException e) {
			e.printStackTrace();
			return false; 

		}
		PPMapParser mapParser = new PPMapParser(MAP_DIR_PATH + fileName);
		PPMapData mapData;
		try {
			mapData = mapParser.parseMapData(fileData);
		} catch (PParseException e) {
			e.printStackTrace();
			return false;

		}
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
