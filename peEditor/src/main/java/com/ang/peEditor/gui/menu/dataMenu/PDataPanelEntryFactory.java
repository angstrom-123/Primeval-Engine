package com.ang.peEditor.gui.menu.dataMenu;

import java.util.ArrayList;
import java.util.List;
import com.ang.peLib.hittables.PSector;
import com.ang.peLib.maths.PVec2;

public class PDataPanelEntryFactory {
	public static List<PDataPanelEntry> newDefaultTopEntries(PVec2 corner, PSector sector,
			int cornerIndex, int sectorIndex) {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_INDEX, PDataPanelEntry.TOP, 
									cornerIndex, sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_X, PDataPanelEntry.TOP, 
									corner.x(), sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_Y, PDataPanelEntry.TOP, 
									corner.y(), sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.CORNER_IS_PORTAL, PDataPanelEntry.TOP, 
									sector.isPortal(cornerIndex), sectorIndex, cornerIndex));
		return out;

	}

	public static List<PDataPanelEntry> newDefaultBottomEntries(PSector sector, 
			int cornerIndex, int sectorIndex) {
		List<PDataPanelEntry> out = new ArrayList<PDataPanelEntry>();
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_INDEX, PDataPanelEntry.BOT, 
									sectorIndex, sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_FLOOR, PDataPanelEntry.BOT, 
									sector.getFloorHeight(), sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_CEILING, PDataPanelEntry.BOT, 
									sector.getCeilingHeight(), sectorIndex, cornerIndex));
		out.add(new PDataPanelEntry(PDataPanelEntryType.SECTOR_LIGHT, PDataPanelEntry.BOT, 
									sector.getLightLevel(), sectorIndex, cornerIndex));
		return out;

	}
}
