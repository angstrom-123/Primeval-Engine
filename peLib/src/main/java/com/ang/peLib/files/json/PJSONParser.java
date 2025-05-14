package com.ang.peLib.files.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ang.peLib.exceptions.PParseException;
import com.ang.peLib.utils.PArrays;

public class PJSONParser {
	private String path;

	public PJSONParser(String path) {
		this.path = path;
	}

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
