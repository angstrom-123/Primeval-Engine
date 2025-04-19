package com.ang.peLib.files.pmap;

import com.ang.peLib.files.PFileReader;
import com.ang.peLib.files.PFileWriter;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.resources.*;

/**
 * Wrapper around various map data methods for ease of use.
 */
public class PPMapHandler {
	private PPMapSaveData saveData = new PPMapSaveData();

	/**
	 * Sets the map data that was saved.
	 * @param mapData the data to be stored as saved
	 * @see 		  PPMapData
	 */
	public void setSavedMapData(PPMapData mapData) {
		saveData.savedMapData = mapData;
	}

	/**
	 * Sets the map data that is editable.
	 * @param mapData the data to be stored as editable
	 * @see			  PPMapData
	 */
	public void setEditableMapData(PPMapData mapData) {
		saveData.editableMapData = mapData;
	}

	/**
	 * Returns the handler's stored data.
	 * @return the data that was stored by the handler
	 * @see    PPMapSaveData
	 */
	public PPMapSaveData getSaveData() {
		return saveData;

	}
	
	/**
	 * Saves the currently editable map data as a file.
	 * The file can either be new or can overwrite an old save if specified. In 
	 * the case where the save should overwrite an old one, the stored filename 
	 * from the save data will be used. In all cases, the map data stored as 
	 * editable will be saved to a file. Then it is set as saved in the save data.
	 * @param  name			      the name to save the new file under. Can be 
	 * 							  {@code null} if the data should overwrite an 
	 * 							  old save; otherwise, the name must be unique
	 * @throws PResourceException if there is a problem with saving the file
	 * @see						  PPMapSaveData 
	 */
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
		String[] PMapData = saveData.editableMapData.toPMap();
		PFileWriter.writeToFile(PResourceType.PMAP, saveName, PMapData);
		saveData.savedMapData = saveData.editableMapData.copy();
		saveData.fileName = name;
	}

	/**
	 * Loads a .pmap file from the map data resource directory into save data.
	 * The specified map file is read, converted to PMap data and then stored as 
	 * saved data in the save data for this handler.
	 * @param  name 			  name of the map file to load
	 * @throws PResourceException if there is a problem with loading the file
	 */
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
