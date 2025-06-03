package com.ang.peLib.hittables;

import com.ang.peLib.maths.*;
import com.ang.peLib.utils.PCopyable;
import com.ang.peLib.graphics.PColour;

/**
 * Hittable object defining a wall of a sector.
 * An edge exists between 2 corners (defined as {@link com.ang.peLib.maths.PVec2}s) 
 * and can be a portal. If an edge is a portal then it has no collision and is not 
 * rendered. It inherits its height from the {@link PSector} that it is a part of. 
 * @see PSector
 * @see com.ang.peLib.maths.PVec2
 */
public class PEdge extends PCopyable {
	private PVec2 p0;
	private PVec2 p1;
	private PColour albedo;
	private PColour upperColour;
	private PColour lowerColour;
	private boolean portal;
	private PVec2 normal;

	/**
	 * Contructs an edge with a colour between 2 points.
	 * @param p0 	 the first corner that bounds the edge
	 * @param p1 	 the second corner that bounds the edge
	 * @param albedo the base colour for the edge
	 * @see 		 com.ang.peLib.graphics.PColour
	 * @see 		 com.ang.peLib.maths.PVec2
	 */
	public PEdge(PVec2 p0, PVec2 p1, PColour albedo) {
		this.p0 = p0;
		this.p1 = p1;
		this.albedo = albedo;
		this.upperColour = albedo;
		this.lowerColour = albedo;
		// clockwise winding
		double dx = p1.x() - p0.x();
		double dy = p1.y() - p0.y();
		this.normal = (new PVec2(dy, -dx)).unitVector();
	}

	/**
	 * Returns the first bounding corner.
	 * @return the first bounding {@link com.ang.peLib.maths.PVec2} 
	 * @see    com.ang.peLib.maths.PVec2
	 */
	public PVec2 getP0() {
		return p0;

	}

	/**
	 * Returns the second bounding corner.
	 * @return the second bounding {@link com.ang.peLib.maths.PVec2} 
	 * @see    com.ang.peLib.maths.PVec2
	 */
	public PVec2 getP1() {
		return p1;

	}

	public PColour getAlbedo() {
		return albedo;

	}

	public PColour getUpperColour() {
		return upperColour;

	}

	public PColour getLowerColour() {
		return lowerColour;

	}

	public PVec2 getNormal() {
		return normal;

	}

	public boolean isPortal() {
		return portal;

	}

	public void setUpperColour(PColour colour) {
		this.upperColour = colour;
	}

	public void setLowerColour(PColour colour) {
		this.lowerColour = colour;
	}
	
	/**
	 * Sets this edge as a portal (no collision and not rendered).
	 */
	public void setAsPortal() {
		portal = true;
	}

	/**
	 * Determines if a ray intersects with the edge within an interval of distance.
	 * @param  r			the {@link com.ang.peLib.maths.PRay} to look for hits with
	 * @param  tInterval	bounds on the distance within which to search for hits
	 * @param  rec			the {@link PHitRecord} to record the hit to
	 * @return 				{@code true} if an intersection is found, else {@code false}
	 * @see 				com.ang.peLib.maths.PRay
	 * @see 				com.ang.peLib.maths.PInterval
	 * @see					PHitRecord
	 */
	public boolean hit(PRay r, PInterval tInterval, PHitRecord rec) {
		PVec2 v1 = r.getOrigin().sub(p0);
		PVec2 v2 = p1.sub(p0);
		PVec2 v3 = new PVec2(-r.getDirection().y(), r.getDirection().x());
		double t1 = PVec2.cross(v2, v1) / PVec2.dot(v2, v3);
		double t2 = PVec2.dot(v1, v3) / PVec2.dot(v2, v3);
		if ((t1 >= 0.0) && (t2 >= 0.0) && (t2 <= 1.0)) {
			if (t1 < tInterval.getMax()) {
				tInterval.setMax(t1);
				rec.setT(t1);
				rec.setColour(albedo);
				rec.setUpperColour(upperColour);
				rec.setLowerColour(lowerColour);
				rec.setBackface(PVec2.dot(r.getDirection(), normal) >= 0.0);
				rec.setPortal(portal);
				return true;

			}
		}
		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PEdge copy() {
		PEdge out = new PEdge(p0.copy(), p1.copy(), albedo.copy());
		if (portal) out.setAsPortal();
		out.setUpperColour(upperColour);
		out.setLowerColour(lowerColour);
		return out;

	}

	/**
	 * Returns a string representation of the edge for debugging.
	 * @return the string representation of the edge
	 */
	@Override
	public String toString() {
		return ("p0:\n" + p0.toString() + "\n" + "p1:\n" + p1.toString());

	}
}
