package com.ang.peEditor;

import java.awt.Color;
import java.util.Map;

import javax.swing.text.AttributeSet.ColorAttribute;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.exceptions.PResourceException;
import com.ang.peLib.exceptions.PResourceExceptionType;
import com.ang.peLib.files.PResourceFileReader;
import com.ang.peLib.files.json.PJSONParser;
import com.ang.peLib.files.json.PJSONValueType;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.maths.PVec3;
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
		Map<String, String> JSONData;
		PJSONParser parser = new PJSONParser(PResourceManager.CONFIG_DIR);
		try {
			JSONData = parser.parseJSONData(lines);
			snapToGrid = (boolean) attemptToRead(JSONData, "snapToGrid", PJSONValueType.BOOLEAN);
			scale = (double) attemptToRead(JSONData, "scale", PJSONValueType.DOUBLE);
			width = (int) attemptToRead(JSONData, "width", PJSONValueType.INTEGER);
			historyLength = (int) attemptToRead(JSONData, "historyLength", PJSONValueType.INTEGER);
			backgroundColour = ((PVec3) attemptToRead(JSONData, "backgroundColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			gridColour = ((PVec3) attemptToRead(JSONData, "gridColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			axisColour = ((PVec3) attemptToRead(JSONData, "axisColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			lineColour = ((PVec3) attemptToRead(JSONData, "lineColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			cornerColour = ((PVec3) attemptToRead(JSONData, "cornerColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			selectedColour = ((PVec3) attemptToRead(JSONData, "selectedColour", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			selectedColour2 = ((PVec3) attemptToRead(JSONData, "selectedColour2", 
					PJSONValueType.DOUBLE_ARRAY)).toColour();
			guiHeadingColour = new Color((int) attemptToRead(JSONData, "guiHeadingColour",
					PJSONValueType.HEXADECIMAL));
			guiBgColour = new Color((int) attemptToRead(JSONData, "guiBgColour",
					PJSONValueType.HEXADECIMAL));
			guiBgColourDark = new Color((int) attemptToRead(JSONData, "guiBgColourDark",
					PJSONValueType.HEXADECIMAL));
			guiInputBgColour = new Color((int) attemptToRead(JSONData, "guiInputBgColour",
					PJSONValueType.HEXADECIMAL));
		} catch (PParseException e) {
			e.printStackTrace();
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
		height = (int) Math.round((double) width / ASPECT_RATIO);
	}

	private Object attemptToRead(Map<String, String> JSONData, String key, PJSONValueType type) 
			throws PParseException {
		String value = JSONData.getOrDefault("user." + key, null);
		if (value == null) {
			value = JSONData.getOrDefault("default." + key, null);
		}
		if (value == null) {
			throw new PParseException("Missing field in editor config file: " + key);

		}
		switch (type) {
		case BOOLEAN:
			return Boolean.valueOf(value);

		case HEXADECIMAL:
			return Integer.valueOf(Integer.parseInt(value, 16));

		case INTEGER:
			return Integer.valueOf(value);

		case DOUBLE:
			return Double.valueOf(value);

		case DOUBLE_ARRAY:
			return stringToArray(value);

		default:
			return null;

		}
	}

	private PVec3 stringToArray(String line) throws PParseException {
		line = line.replace("[", "");
		line = line.replace("]", "");
		String[] values = line.split(",");
		if (values.length != 3) {
			throw new PParseException("Arrays must have 3 elements");

		}
		PVec3 out = new PVec3(Double.valueOf(values[0]), Double.valueOf(values[1]), 
							  Double.valueOf(values[2]));
		return out;

	}
}
