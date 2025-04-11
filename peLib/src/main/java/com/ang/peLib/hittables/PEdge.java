package com.ang.peLib.hittables;

import com.ang.peLib.maths.*;
import com.ang.peLib.graphics.PColour;

public class PEdge {
	private PVec2 p0;
	private PVec2 p1;
	private PColour albedo;
	private boolean portal;

	public PEdge(PVec2 p0, PVec2 p1, PColour albedo) {
		this.p0 = p0;
		this.p1 = p1;
		this.albedo = albedo;
	}

	public PVec2 getP0() {
		return p0;

	}

	public PVec2 getP1() {
		return p1;

	}
	
	public void setAsPortal() {
		portal = true;
	}

	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		if (portal) {
			return false;

		}
		PVec2 v1 = r.getOrigin().sub(p0);
		PVec2 v2 = p1.sub(p0);
		PVec2 v3 = new PVec2(-r.getDirection().y(), r.getDirection().x());
		double t1 = PVec2.cross(v2, v1) / PVec2.dot(v2, v3);
		double t2 = PVec2.dot(v1, v3) / PVec2.dot(v2, v3);
		if ((t1 >= 0.0) && (t2 >= 0.0) && (t2 <= 1.0)) {
			if (t1 < tInterval.max()) {
				tInterval.setMax(t1);
				rec.setT(t1);
				rec.setColour(albedo);
				return true;

			}
		}
		return false;

	}

	@Override
	public String toString() {
		return ("p0:\n" + p0.toString() + "\n" + "p1:\n" + p1.toString());

	}
}
