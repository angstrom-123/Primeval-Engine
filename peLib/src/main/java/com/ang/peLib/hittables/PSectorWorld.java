package com.ang.peLib.hittables;

import com.ang.peLib.maths.*;
import com.ang.peLib.utils.*;

public class PSectorWorld extends PCopyable {
	private PSector[] sectors;
	private int maxSectors;
	private int head = 0;

	public PSectorWorld(int maxSectors) {
		this.maxSectors = maxSectors;
		sectors = new PSector[maxSectors];
	}


	@Override
	@SuppressWarnings("unchecked")
	public PSectorWorld copy() {
		PSectorWorld temp = new PSectorWorld(maxSectors);
		for (int i = 0; i < head; i++) {
			temp.addSector(sectors[i]);
		}
		return temp;

	}
	
	public void addSector(PSector sec) {
		if (head < maxSectors) {
			sectors[head++] = sec;
		} else {
			System.out.println("Failed to add sector to world");
		}
	}

	public int getMaxSectors() {
		return maxSectors;

	}

	public PSector[] getSectors() {
		return (PSector[]) PArrays.reduceArray(sectors, head, PSector.class);

	}

	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		boolean didHit = false;
		double closestHit = Double.MAX_VALUE;
		PHitRecord tempRec = new PHitRecord();
		for (int i = 0; i < head; i++) {
			PSector sec = sectors[i];
			PInterval bounds = new PInterval(tInterval.min(), closestHit);
			if (sec.hit(r, bounds, tempRec)) {
				if ((tempRec.getT() >= 0.0) && (tempRec.getT() < tInterval.max())) {
					didHit = true;
					closestHit = tempRec.getT();
					rec.setT(tempRec.getT());
					rec.setColour(tempRec.getColour());
					rec.setFloor(tempRec.getFloorHeight());
					rec.setCeiling(tempRec.getCeilingHeight());
				}
			}
		}
		return didHit;

	}

	public PHitRecord[] allHits(PRay r, PInterval tInterval) {
		PHitRecord[] hits = new PHitRecord[100];
		int hitsHead = 0;
		for (int i = 0; i < head; i++) {
			PSector sec = sectors[i];
			for (PHitRecord rec : sec.allHits(r, tInterval.copy())) {
				if ((rec.getT() >= 0.0) & (rec.getT() < tInterval.max())) {
					hits[hitsHead++] = rec;
				}
			}
		}
		return (PHitRecord[]) PArrays.reduceArray(hits, hitsHead, PHitRecord.class);

	}

	@Override 
	public String toString() {
		String out = "";
		for (int i = 0; i < head; i++) {
			out += "(\n" + sectors[i].toString() + "\n)";
		}
		return out;

	}
}
