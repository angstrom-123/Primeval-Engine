package com.ang.peLib.files.pmap;

/**
 * Stores map data for editing.
 * Stores the currently editable data, saved data, and the filename of the 
 * saved data ({@code null} if it has not been saved)
 */
public class PPMapSaveData {
	public PPMapData savedMapData;
	public PPMapData editableMapData;
	public String fileName;
}
