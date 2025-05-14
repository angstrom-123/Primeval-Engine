package com.ang.peLib.files.json;

import java.util.HashMap;
import java.util.Map;

public class PJSONObject {
	public Map<String, String> valueMap = new HashMap<String, String>();
	public String name;
	public PJSONObject[] parents;

	public PJSONObject(String name, PJSONObject parent) {
		this.name = name;
		if (parent != null) {
			this.parents = new PJSONObject[parent.parents.length + 1];
			this.parents[0] = parent;
			for (int i = 0; i < parent.parents.length; i++) {
				PJSONObject obj = parent.parents[i];
				if (obj == null) break;

				this.parents[i + 1] = obj;
			}
		} else {
			this.parents = new PJSONObject[]{null};
		}
	}

	public void addToMap(String key, String value) {
		String entryKey = "";
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i] == null) continue;

			entryKey += parents[i].name + ".";
		}
		entryKey += name + "." + key;
		valueMap.put(entryKey, value);
	}

	@Override
	public String toString() {
		String out = name;
		for (PJSONObject parent : this.parents) {
			if (parent == null) continue;

			out += " > " + parent.name;
		}
		for (String key : valueMap.keySet()) {
			out += "\n	" + key + ": " + valueMap.get(key);
		}
		return out;

	}
}
