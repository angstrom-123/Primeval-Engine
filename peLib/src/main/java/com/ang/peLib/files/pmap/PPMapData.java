package com.ang.peLib.files.pmap;

import com.ang.peLib.hittables.PSector;
import com.ang.peLib.hittables.PSectorWorld;
import com.ang.peLib.maths.PVec2;

public class PPMapData {
	public PSectorWorld world;
	public PVec2 position;
	public PVec2 facing;

	public PPMapData() {}

	public PPMapData copy() {
		PPMapData temp = new PPMapData();
		temp.world = world.copy();
		temp.position = position.copy();
		temp.facing = facing.copy();
		return temp;

	}

	public boolean isPopulated() {
		if ((world != null) && (position != null) && (facing != null)) {
			return true;

		}
		return false;

	}

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
		lines[head++] = "!COLOUR";
		lines[head++] = "1.0 1.0 1.0"; // TODO: implement for real
		return lines;

	}

	private int calculateFileLength() {
		int count = 8;
		count += 2;
		for (PSector sec : world.getSectors()) {
			count += 2;
			count += sec.getPortalIndices().length / 2;
			count += sec.getCorners().length;
		}
		count += 1; // TODO: temporarily adding 1 colour, implement this later
		return count;

	}
}
