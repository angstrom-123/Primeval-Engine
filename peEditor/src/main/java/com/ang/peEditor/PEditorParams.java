package com.ang.peEditor;

import java.awt.Color;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.exceptions.PResourceExceptionType;
import com.ang.peLib.files.PResourceFileReader;
import com.ang.peLib.files.json.PJSONExtractor;
import com.ang.peLib.files.json.PJSONParser;
import com.ang.peLib.files.json.PJSONValueType;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.resources.PResource;
import com.ang.peLib.resources.PResourceManager;
import com.ang.peLib.resources.PResourceType;

public class PEditorParams {
	public final double ASPECT_RATIO 	= 16.0 / 9.0;
	public final int 	CORNER_SIZE 	= 8;
	public final int 	CORNER_RADIUS 	= 6;
	public boolean 		snapToGrid;
	public double 		scale;
	public int 			width;
	public int 			height;
	public int			historyLength;
	public PColour 		backgroundColour;
	public PColour 		gridColour;
	public PColour 		axisColour;
	public PColour 		lineColour;
	public PColour 		cornerColour;
	public PColour 		selectedColour;
	public PColour 		selectedColour2;
	public Color		guiHeadingColour;
	public Color 		guiBgColour;
	public Color 		guiBgColourDark;
	public Color		guiInputBgColour;

	public void init() throws PResourceException {
		PResource res = PResourceManager.fetch(PResourceType.CONFIG, "config.json");
		PResourceFileReader reader = new PResourceFileReader();
		String[] lines = reader.readFile(PResourceType.CONFIG, "config.json");
		PJSONParser parser = new PJSONParser(PResourceManager.CONFIG_DIR);
		try {
			PJSONExtractor extractor = new PJSONExtractor(parser.parseJSONData(lines));
			snapToGrid = (boolean) extractor.tryToExtract(
				"snapToGrid", 
				PJSONValueType.BOOLEAN);
			scale = (double) extractor.tryToExtract(
				"scale", 
				PJSONValueType.DOUBLE);
			width = (int) extractor.tryToExtract(
				"width", 
				PJSONValueType.INTEGER);
			historyLength = (int) extractor.tryToExtract(
				"historyLength", 
				PJSONValueType.INTEGER);
			backgroundColour = (PColour) extractor.tryToExtract(
				"backgroundColour", 
				PJSONValueType.PCOLOUR);
			gridColour = (PColour) extractor.tryToExtract(
				"gridColour", 
				PJSONValueType.PCOLOUR);
			axisColour = (PColour) extractor.tryToExtract(
				"axisColour", 
				PJSONValueType.PCOLOUR);
			lineColour = (PColour) extractor.tryToExtract(
				"lineColour", 
				PJSONValueType.PCOLOUR);
			cornerColour = (PColour) extractor.tryToExtract(
				"cornerColour", 
				PJSONValueType.PCOLOUR);
			selectedColour = (PColour) extractor.tryToExtract(
				"selectedColour", 
				PJSONValueType.PCOLOUR);
			selectedColour2 = (PColour) extractor.tryToExtract(
				"selectedColour2", 
				PJSONValueType.PCOLOUR);
			guiHeadingColour = new Color((int) extractor.tryToExtract(
				"guiHeadingColour", 
				PJSONValueType.HEXADECIMAL));
			guiBgColour = new Color((int) extractor.tryToExtract(
				"guiBgColour", 
				PJSONValueType.HEXADECIMAL));
			guiBgColourDark = new Color((int) extractor.tryToExtract(
				"guiBgColourDark", 
				PJSONValueType.HEXADECIMAL));
			guiInputBgColour = new Color((int) extractor.tryToExtract(
				"guiInputBgColour", 
				PJSONValueType.HEXADECIMAL));
		} catch (PParseException e) {
			e.printStackTrace();
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
		height = (int) Math.round((double) width / ASPECT_RATIO);
	}
}
