package com.ang.peCore;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.exceptions.PResourceExceptionType;
import com.ang.peLib.files.PResourceFileReader;
import com.ang.peLib.files.json.PJSONExtractor;
import com.ang.peLib.files.json.PJSONParser;
import com.ang.peLib.files.json.PJSONValueType;
import com.ang.peLib.resources.PResource;
import com.ang.peLib.resources.PResourceManager;
import com.ang.peLib.resources.PResourceType;

/**
 * Handles the config params for the game.
 */
public class PGameParams {
	public int frameRate = 60;
	public int imageWidth = 320;
	public double aspectRatio;
	public double scale = 4.0;
	public double fov;
	public int imageHeight;

	/**
	 * Attempts to parse the json config file in the game's resources.
	 * Reads all required variables from the config. A user can set their own 
	 * preferences. Otherwise, default parameters are loaded. If neither are dfound,
	 * then the exception is thrown.
	 * @throws PResourceException if there is a problem with parsing the config file 
	 */
	public void init() throws PResourceException {
		PResource res = PResourceManager.fetch(PResourceType.CONFIG, "config.json");
		PResourceFileReader reader = new PResourceFileReader();
		String[] lines = reader.readFile(PResourceType.CONFIG, "config.json");
		PJSONParser parser = new PJSONParser(PResourceManager.CONFIG_DIR);
		try {
			PJSONExtractor extractor = new PJSONExtractor(parser.parseJSONData(lines));
			frameRate = (int) extractor.tryToExtract(
				"frameRate", 
				PJSONValueType.INTEGER);
			imageWidth = (int) extractor.tryToExtract(
				"imageWidth", 
				PJSONValueType.INTEGER);
			double aspectNumerator = (double) extractor.tryToExtract(
				"aspectNumerator", 
				PJSONValueType.DOUBLE);
			double aspectDenominator = (double) extractor.tryToExtract(
				"aspectDenominator", 
				PJSONValueType.DOUBLE);
			aspectRatio = aspectNumerator / aspectDenominator;
			scale = (double) extractor.tryToExtract(
				"scale", 
				PJSONValueType.DOUBLE);
			double fovDenominator = (double) extractor.tryToExtract(
				"fovDenominator", 
				PJSONValueType.DOUBLE);
			fov = Math.PI / fovDenominator;
		} catch (PParseException e ) {
			e.printStackTrace();
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
		imageHeight = (int) Math.round((double) imageWidth / aspectRatio);
	}
}
