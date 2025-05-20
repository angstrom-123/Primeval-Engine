package com.ang.peLib.files.pmap;

import com.ang.peLib.exceptions.*;
import com.ang.peLib.graphics.PColour;
import com.ang.peLib.utils.PArrays;
import com.ang.peLib.hittables.*;
import com.ang.peLib.maths.*;

/**
 * Converts data from a .pmap file to a {@link PPMapData} object.
 * @see PPMapData
 */
public class PPMapParser {
	private String path;

	/**
	 * Constructor with a path to the map files.
	 * @param path the path (starting from the resources directory) to map file 
	 * 			   that will be parsed by this instance. This is only used for 
	 * 			   reporting errors.
	 */
	public PPMapParser(String path) {
		this.path = path;
	}

	/**
	 * Returns the data from a .pmap file as {@link PPMapData}.
	 * Parsing will fail if any of the required fields are missing from the data 
	 * passed in. Fields that must be in a .pmap file are found in the spec for 
	 * {@link PPMapData#copy()}
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @return 				   {@link PPMapData} storing the specified .pmap data
	 * @throws PParseException if there is a problem with parsing the data
	 * @see 				   PPMapData
	 */
	public PPMapData parseMapData(String[] lines) throws PParseException {
		PPMapData mapData = new PPMapData();
		mapData.position = extractOneVec2(lines, "!POSITION");
		mapData.facing = extractOneVec2(lines, "!FACING");
		if (lines[0].equals("!PMAPv1.0.0")) {
			mapData.world = parseWorld(lines);
		} else {
			throw new PParseException(path, 0);

		}
		return mapData;

	}

	/**
	 * Parses a {@link com.ang.peLib.hittabes.PSectorWorld} from .pmap data.
	 * Data is extracted under predefined headings. A list of all headings is 
	 * found in the spec for {@link PPMapData#copy()}. The headings required 
	 * to construct a {@link com.ang.peLib.maths.PSectorWorld}
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @return 				   {@link com.ang.peLib.maths.PSectorWorld} containing 
	 * 						   the data found in the file
	 * @throws PParseException if there is a problem with parsing the data
	 * @see 				   com.ang.peLib.maths.PSectorWorld
	 * @see 				   com.ang.peLib.maths.PVec2
	 * @see 				   PPMapData
	 */
	private PSectorWorld parseWorld(String[] lines) throws PParseException {
		PVec2[] corners = new PVec2[0];
		int[] sectors = new int[0];
		PVec2[] heights = new PVec2[0];
		int[] portals = new int[0];
		PColour[] colours = new PColour[0];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.equals("!CORNER")) {
				corners = extractVec2s(i + 1, lines);
			} else if (line.equals("!SECTOR")) {
				sectors = extractInts(i + 1, lines);	
			} else if (line.equals("!HEIGHT")) {
				heights = extractVec2s(i + 1, lines);	
			} else if (line.equals("!PORTAL")) {
				portals = extractInts(i + 1, lines);
			} else if (line.equals("!COLOUR")) {
				colours = extractColours(i + 1, lines);	
			}
		}
		if ((corners.length == 0) || (sectors.length == 0) || (colours.length == 0)) {
			throw new PParseException(path, 0);

		}
		return constructWorld(corners, sectors, heights, portals, colours);

	}

	/**
	 * Constructs a new {@link com.ang.peLib.hittables.PSectorWorld} using data 
	 * extracted from a .pmap file.
	 * @param  corners 		   coordinates of each corner
	 * @param  sectors		   indices into specified corners that are the first 
	 * 						   corner in a new sector (sector delimiters)
	 * @param  heights 		   vectors representing the floor height in the x value 
	 * 						   and the ceiling height in the y value for each sector
	 * @param  portals 		   indices into the specified corners that are the 
	 * 						   bounds of a portal
	 * @param  colours 		   the colours to be used in the world (not currently used)
	 * @return 				   a new {@link com.ang.peLib.hittables.PSectorWorld}
	 * 						   constructed from the specified data
	 * @throws PParseException if there is a problem with constructing the 
	 * 						   {@link com.ang.peLib.hittables.PSectorWorld}
	 * @see 				   com.ang.peLib.hittables.PSectorWorld
	 * @see 				   com.ang.peLib.hittables.PSector
	 * @see 				   com.ang.peLib.maths.PVec2
	 */
	private PSectorWorld constructWorld(PVec2[] corners, int[] sectors, PVec2[] heights, 
			int[] portals, PColour[] colours) throws PParseException {
			// PVec2[] portals, PColour[] colours) throws PParseException {
		PSectorWorld world = new PSectorWorld(1000); // arbitrary size limit
		for (int i = 0; i < sectors.length; i++) {
			// get sector limits
			int limit = (i == sectors.length - 1)
			? corners.length
			: sectors[i + 1];
			// read corners within limits
			PVec2[] sectorCorners = new PVec2[limit - sectors[i]];
			for (int j = sectors[i]; j < limit; j++) {
				sectorCorners[j - sectors[i]] = corners[j];	
			}
			// read portals within limits
			int[] sectorPortals = new int[sectorCorners.length];
			int head = 0;
			for (int j = 0; j < portals.length; j++) {
				if ((portals[j] >= sectors[i]) && (portals[j] < limit)) {
					sectorPortals[head++] = portals[j] - sectors[i];
				}
			}
			sectorPortals = PArrays.reduceArray(sectorPortals, head);
			// create and add sector
			PSector sec = new PSector(sectorCorners, sectorPortals);	
			sec.setHeight(heights[i].x(), heights[i].y());
			world.addSector(sec);
		}
		return world;

	}

	/**
	 * Parses integers starting from a specific line.
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @param  startLine 	   the line number that contains the heading under 
	 * 						   which the integers should be parsed
	 * @return 				   integer array containing the data found under the 
	 * 						   heading
	 * @throws PParseException if there is a problem with parsing the data
	 * @see 				   PPMapData
	 */
	private int[] extractInts(int startLine, String[] lines) throws PParseException {
		int[] array = new int[lines.length - startLine];
		int head = 0;
		for (int i = startLine; i < lines.length; i++) {
			String line = lines[i];
			if (line.charAt(0) == '!') {
				// if (head == 0) {
				// 	throw new PParseException(path, i);
				//
				// }
				break;

			}
			String[] nums = line.split("\\s+");
			if (nums.length != 1) {
				throw new PParseException(path, i);

			}
			int num = Integer.valueOf(nums[0]);
			array[head++] = num;
		}
		return PArrays.reduceArray(array, head);

	}

	/**
	 * Parses a single {@link com.ang.peLib.maths.PVec2} from under a heading.
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @param  match 		   heading that the data should be found under. 
	 * 						   The headings in a .pmap file are found in the 
	 * 						   spec for {@link PPMapData#copy()}.
	 * @return 				   {@link com.ang.peLib.maths.PVec2} containing the 
	 * 						   data found under the heading
	 * @throws PParseException if there is a problem with parsing the data
	 * @see 				   com.ang.peLib.maths.PVec2
	 * @see 				   PPMapData
	 */
	private PVec2 extractOneVec2(String[] lines, String match) throws PParseException {
		PVec2[] extracted = new PVec2[0];
		int lineNum = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.equals(match)) {
				extracted = extractVec2s(i + 1, lines);	
				lineNum = i + 1;
				break;

			}
		}
		if (extracted.length != 1) {
			throw new PParseException(path, lineNum);

		}
		return extracted[0];

	}

	/**
	 * Parses {@link com.ang.peLib.maths.PVec2}s starting from a specific line.
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @param  startLine 	   the line number that contains the heading under 
	 * 						   which the vectors should be parsed
	 * @return 				   vector array containing the data found under the 
	 * 						   heading
	 * @throws PParseException if there is a problem with parsing the data
	 * @see 				   com.ang.peLib.maths.PVec2
	 * @see 				   PPMapData
	 */
	private PVec2[] extractVec2s(int startLine, String[] lines) 
			throws PParseException {
		PVec2[] array = new PVec2[lines.length - startLine];
		int head = 0;
		for (int i = startLine; i < lines.length; i++) {
			String line = lines[i];
			if (line.charAt(0) == '!') {
				break;

			}
			String[] nums = line.split("\\s+");
			if (nums.length != 2) {
				throw new PParseException(path, i);

			}
			double x = Double.valueOf(nums[0]);
			double y = Double.valueOf(nums[1]);
			array[head++] = new PVec2(x, y);
		}
		return PArrays.reduceArray(array, head, PVec2.class);

	}

	/**
	 * Parses {@link com.ang.peLib.graphics.PColour}s starting from a specific line.
	 * @param  lines		   array of strings representing each line from a 
	 * 						   .pmap file
	 * @param  startLine 	   the line number that contains the heading under 
	 * 						   which the colours should be parsed
	 * @return 				   colour array containing the data found under the 
	 * 						   heading
	 * @throws PParseException if there is a problem with parsing the data
	 * @see					   com.ang.peLib.graphics.PColour
	 * @see 				   PPMapData
	 */
	private PColour[] extractColours(int startLine, String[] lines) 
			throws PParseException {
		PColour[] array = new PColour[lines.length - startLine];
		int head = 0;
		for (int i = startLine; i < lines.length; i++) {
			String line = lines[i];
			if (line.charAt(0) == '!') {
				if (head == 0) {
					throw new PParseException(path, i);

				}
				return new PColour[0];	

			}
			String[] nums = line.split("\\s+");
			if (nums.length != 3) {
				throw new PParseException(path, i);

			}
			double r = Double.valueOf(nums[0]);
			double g = Double.valueOf(nums[1]);
			double b = Double.valueOf(nums[2]);
			array[head++] = new PColour(r, g, b);
		}
		return PArrays.reduceArray(array, head, PColour.class);

	}
}
