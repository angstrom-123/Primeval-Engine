package com.ang.peLib.files.pmap;

import com.ang.peLib.files.PFileReader;
import com.ang.peLib.files.PFileWriter;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.resources.*;

public class PPMapHandler {
	private PPMapSaveData saveData;

	public PPMapHandler() {
		saveData = new PPMapSaveData();
	}

	public void setSavedMapData(PPMapData mapData) {
		saveData.savedMapData = mapData;
	}

	public void setEditableMapData(PPMapData mapData) {
		saveData.editableMapData = mapData;
	}

	public PPMapSaveData getSaveData() {
		return saveData;

	}
	
	public void saveMap(String name) throws PResourceException {
		String saveName = name;
		if (name == null) {
			System.out.println(saveData.fileName);
			saveName = saveData.fileName;
		}
		PResource res = PResourceManager.fetchResource(PResourceType.PMAP, saveName);
		if (!res.valid()) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		if (res.exists() && (name != null)) {
			throw new PResourceException(res, PResourceExceptionType.ALREADY_EXISTS);
			
		}
		if (name != null) {
			PFileWriter.newFile(PResourceType.PMAP, saveName);
		}
		PFileWriter.writeToFile(PResourceType.PMAP, saveName, 
				saveData.editableMapData.toPMap());
		saveData.savedMapData = saveData.editableMapData.copy();
		saveData.fileName = name;
	}

	public void loadMapData(String name) throws PResourceException {
		PResource res = PResourceManager.fetchResource(PResourceType.PMAP, name);
		if (!res.valid()) {
			throw new PResourceException(res, PResourceExceptionType.INVALID);

		}
		if (!res.exists()) {
			throw new PResourceException(res, PResourceExceptionType.NOT_FOUND);
			
		}
		String[] lines;
		PFileReader reader = new PFileReader();
		lines = reader.readFile(PResourceType.PMAP, name);
		PPMapData mapData;
		PPMapParser parser = new PPMapParser(PResourceManager.MAP_DIR + name);
		try {
			mapData = parser.parseMapData(lines);
		} catch (PParseException e) {
			throw new PResourceException(res, PResourceExceptionType.READ_FAIL);

		}
		saveData.editableMapData = mapData;
		saveData.savedMapData = mapData.copy();
		saveData.fileName = name;
	}
}
