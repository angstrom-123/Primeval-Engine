package com.ang.peLib.files.pmap;

import com.ang.peLib.utils.PCopyable;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.maths.PVec2;

/**
 * Holds data about a map and provides utilities for managing the map data.
 */
public class PPMapData extends PCopyable {
	public PSectorWorld world;
	public PVec2 position;
	public PVec2 facing;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PPMapData copy() {
		PPMapData temp = new PPMapData();
		temp.world = world.copy();
		temp.position = position.copy();
		temp.facing = facing.copy();
		return temp;

	}

	/**
	 * Checks if all fields are populated.
	 * A field is populated if its value is not {@code null}.
	 * @return {@code true} if all fields are populated, else {@code false}
	 */
	public boolean isPopulated() {
		return ((world != null) && (position != null) && (facing != null));

	}

	/**
	 * Converts the map data in this instance to an array of Strings in the .pmap
	 * format for writing to files.
	 * The .pmap format specifies: 
	 * <ul>
	 * <li>The coordinates of each corner, ordered clockwise and grouped by sector
	 * <li>The indices of the corners that start a new sector
	 * <li>The floor and ceiling heights for each sector
	 * <li>Pairs of indices that have a portal (intangible wall segment) between them
	 * <li>The coordinates of the player's starting position
	 * <li>The camera's starting orientation vector
	 * <li>The colours that are in the level (field is not currently used)
	 * </ul>
	 * <p>
	 * The data is sorted under headings preceded by an exclamation mark. The 
	 * headings are as follows: 
	 * <ul>
	 * <li>Version: 		  "!PMAPv1.0.0"
	 * <li>Corner positions:  "!CORNER"
	 * <li>Sector delimiters: "!SECTOR"
	 * <li>Sector heights:	  "!HEIGHT"
	 * <li>Portal indives:	  "!PORTAL"
	 * <li>Player position:   "!POSITION"
	 * <li>Camera direction:  "!FACING"
	 * <li>Colours:			  "!COLOUR"
	 * </ul>
	 * These headings do not have to be defined in order 
	 * <strong>except for the version which must be at the top</strong>
	 * @return an array of strings storing each line for a .pmap file representing
	 * 		   the data stored in this instance
	 */
	public String[] toPMap() {
		String[] lines = new String[calculateFileLength()];
		int head = 0;
		lines[head++] = "!PMAPv1.0.0";
		lines[head++] = "!CORNER";
		for (PSector sec : world.getSectors()) {
			for (PVec2 vec : sec.getCorners()) {
				lines[head++] = String.valueOf(vec.x()) + " " + String.valueOf(vec.y());
			}
		}
		lines[head++] = "!SECTOR";
		lines[head++] = "0";
		int sectorStart = 0;
		for (int i = 0; i < world.getSectors().length - 1; i++) {
			PSector sec = world.getSectors()[i];
			sectorStart += sec.getCorners().length;
			lines[head++] = String.valueOf(sectorStart);
		}
		lines[head++] = "!HEIGHT";
		for (PSector sec : world.getSectors()) {
			lines[head++] = String.valueOf(sec.getFloorHeight()) 
					+ " " + String.valueOf(sec.getCeilingHeight());
		}
		lines[head++] = "!PORTAL";
		for (PSector sec : world.getSectors()) {
			for (int i = 0; i < sec.getPortalIndices().length - 1; i++) {
				lines[head++] = String.valueOf(sec.getPortalIndices()[i])
						+ " " + String.valueOf(sec.getPortalIndices()[i + 1]);
			}
		}
		lines[head++] = "!POSITION";
		lines[head++] = String.valueOf(position.x())
				+ " " + String.valueOf(position.y());
		lines[head++] = "!FACING";
		lines[head++] = String.valueOf(facing.x())
				+ " " + String.valueOf(facing.y());
		// TODO: implement for real
		lines[head++] = "!COLOUR";
		lines[head++] = "1.0 1.0 1.0";
		return lines;

	}

	/**
	 * Calculates the amount of lines that the generated .pmap file will have
	 * @return the amount of lines in the .pmap file for the data in this instance
	 */
	private int calculateFileLength() {
		int count = 8;
		count += 2;
		for (PSector sec : world.getSectors()) {
			count += 2;
			count += sec.getPortalIndices().length / 2;
			count += sec.getCorners().length;
		}
		// TODO: temporarily adding 1 colour, implement
		count += 1;
		return count;

	}
}
