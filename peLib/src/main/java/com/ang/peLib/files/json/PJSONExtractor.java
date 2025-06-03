package com.ang.peLib.files.json;

import java.util.Map;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.maths.PVec3;

/**
 * Provides utilities for extracting values from json data.
 */
public class PJSONExtractor {
	Map<String, String> jsonData;

	/**
	 * Constructs a new extracter with a map of data to parse.
	 * @param jsonData key value pairs representing a json file
	 */
	public PJSONExtractor(Map<String, String> jsonData) {
		this.jsonData = jsonData;
	}

	/**
	 * Attempts to extract a given variable from the file.
	 * First attempts to read the variable under the "user" object, then falls 
	 * back to "default"; if neither are found then an error is thrown.
	 * @param  key			   key for the requested value
	 * @param  type 		   supported json datatype represented by the value
	 * @return 				   value held behind the key in the data as an object
	 * @throws PParseException if the requested key cannot be extracted from the file
	 */
	public Object tryToExtract(String key, PJSONValueType type) throws PParseException {
		String user = jsonData.getOrDefault("user." + key, null);
		String def = jsonData.getOrDefault("default." + key, null);
		String val;
		if (user != null) {
			val = user;
		} else if (def != null) {
			val = def;
		} else {
			throw new PParseException("Missing field in JSON data: " + key);

		}
		switch (type) {
			case BOOLEAN -> { return Boolean.valueOf(val); }
			case HEXADECIMAL -> { return Integer.valueOf(Integer.parseInt(val, 16)); }
			case INTEGER -> { return Integer.valueOf(val); }
			case DOUBLE -> { return Double.valueOf(val); }
			case PVEC3 -> {
				double[] nums = arrayFromString(val);
				return new PVec3(nums);
			}
			case PCOLOUR -> {
				double[] nums = arrayFromString(val);
				return new PColour(nums);
			}
		}
		throw new PParseException("Invalid value for key: " + key + " value: " + val);

	}

	/**
	 * Converts a string representing an array to an array.
	 * @param  line the string representing the array 
	 * @return 		array represented by the line, this is always a double[] 
	 */
	private double[] arrayFromString(String line) {
		line = line.replace("[", "");
		line = line.replace("]", "");
		String[] values = line.split(",");
		double[] out = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			out[i] = Double.valueOf(values[i].strip());
		}
		return out;

	}
}
