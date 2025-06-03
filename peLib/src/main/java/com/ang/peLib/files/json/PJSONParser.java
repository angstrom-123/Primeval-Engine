package com.ang.peLib.files.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.utils.PArrays;

/**
 * Converts data from a .json file to a map of key value pairs.
 */
public class PJSONParser {
	private String path;

	/**
	 * Constructs a new parser with a path.
	 * @param path the path of the file to parse (used in error messages)
	 */
	public PJSONParser(String path) {
		this.path = path;
	}

	/**
	 * Converts the input lines representing a JSON file to a map of key value pairs.
	 * @param  lines 		   array of lines representing a json file
	 * @return 				   map of key value pairs extracted from the file
	 * @throws PParseException if there is a problem with parsing the file
	 */
	public Map<String, String> parseJSONData(String[] lines) throws PParseException {
		lines = stripLines(lines);
		lines = stripStartAndEnd(lines);
		PJSONObject[] objects = getJSONObjects(lines);
		Map<String, String> out = new HashMap<String, String>();
		for (PJSONObject obj : objects) {
			out.putAll(obj.valueMap);
		}
		return out;

	}

	/**
	 * Constructs a hierarchy of json objects representing the organization of 
	 * the json file.
	 * @param  lines 		   array of json data lines to parse
	 * @return 				   array of json objects representing the file
	 * @throws PParseException if there is a problem with parsing the json data
	 * @see    PJSONObject
	 */
	private PJSONObject[] getJSONObjects(String[] lines) throws PParseException {
		PJSONObject[] objects = new PJSONObject[lines.length / 2];
		int head = 0;
		List<Integer> open = new ArrayList<Integer>();
		for (int i = 0; i < lines.length; i++) {
			String name = getNameFromLine(lines[i]);
			String value = getValueFromLine(lines[i]);
			if (lines[i].endsWith("{")) {
				if (name == null) {
					throw new PParseException(path, i);

				}
				if (open.isEmpty()) {
					objects[head++] = new PJSONObject(name, null);
				} else {
					objects[head++] = new PJSONObject(name, objects[open.getLast()]);
				}
				open.add(head - 1);
			} else if (lines[i].endsWith("},") || lines[i].endsWith("}")) {
				open.removeLast();
			} else if ((name != null) && (value != null)) {
				objects[open.getLast()].addToMap(name, value);
			}
		}
		return PArrays.reduceArray(objects, head, PJSONObject.class);

	}

	/**
	 * Extracts the value from a line.
	 * @param  line the line to get the value from 
	 * @return 		value in the line
	 */
	private String getValueFromLine(String line) {
		String value = "";
		boolean found = false;
		for (char c : line.toCharArray()) {
			if (c == '"') continue;

			if (found) {
				value += c;
			}
			if (c == ':') found = true;
		}
		if (value.endsWith(",")) value = value.substring(0, value.length() - 1);
		return (found) ? value.strip() : null;

	}

	/**
	 * Extracts the name of a field or class from a line.
	 * @param  line the line to get the name from 
	 * @return 		name of the field or object in the line
	 */
	private String getNameFromLine(String line) {
		String name = "";
		boolean found = false;
		for (char c : line.toCharArray()) {
			if (c == '"') continue;

			if (c == ':') {
				found = true;
				break;

			}

			name += c;
		}
		return (found) ? name.strip() : null;

	}

	/**
	 * Removes initial curly braces from the start and end of the file.
	 * @param  lines 		   the lines to remove the curly braces from 
	 * @return 		 		   lines with the first and last lines removed
	 * @throws PParseException if the file does not start and end with curly braces
	 */
	private String[] stripStartAndEnd(String[] lines) throws PParseException {
		if (!lines[0].equals("{") || !lines[lines.length - 1].equals("}")) {
			throw new PParseException(path, 0);	

		}
		String[] out = new String[lines.length - 2];
		for (int i = 0; i < out.length; i++) {
			out[i] = lines[i + 1];	
		}
		return out;

	}

	/**
	 * Strips white space from either side of all lines.
	 * @param  lines the lines to strip
	 * @return 		 lines stripped of leading and following whitespace and 
	 * 				 stripped of leading and following newlines
	 */
	private String[] stripLines(String[] lines) {
		String[] out = new String[lines.length];
		int j = 0;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].isBlank()) continue;

			out[j] = lines[i].strip();	
			j++;
		}
		return PArrays.reduceArray(out, j, String.class);

	}
}
