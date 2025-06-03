package com.ang.peCore;

import java.util.Comparator;

import com.ang.peLib.graphics.*;
import com.ang.peLib.maths.*;
import com.ang.peLib.utils.PCopyableSorter;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.PFullKeyboardInputListener;

public class PCamera {
	private PColour backgroundCol = new PColour(0.3, 0.4, 0.6);
	private PVec2 position = new PVec2(0.0, 0.0);
	private PVec2 facing = new PVec2(0.0, 1.0);
	private int renderMode = 0;
	private double elevation = 0.0;
	private PGameParams params;
	private double viewportHeight;
	private double viewportWidth;
	private PVec3 w, u, v;
	private PVec2 pixel0Position;
	private PVec2 pixelDeltaU;
	private PRenderer renderer;
	private PCopyableSorter<PHitRecord> recSorter;
	
	public PCamera(PGameParams params) {
		this.params = params;
	}

	public PRenderer getRenderer() {
		return renderer;

	}

	public void setTransform(PVec2 position, PVec2 facing) {
		this.position = position;
		this.facing = facing;
	}

	public void changePosition(PVec2 positionDelta) {
		PVec2 xDelta = u.toVec2().neg().mul(positionDelta.x());
		PVec2 yDelta = facing.mul(positionDelta.y());
		position = position.add(xDelta).add(yDelta);
	}

	public void changeFacing(double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double xPos = facing.x() * cosTheta - facing.y() * sinTheta;
		double yPos = facing.x() * sinTheta + facing.y() * cosTheta;
		facing = new PVec2(xPos, yPos).unitVector();
	}

	public void changeElevation(double delta) {
		elevation += delta;
		update();
	}

	public void init(PFullKeyboardInputListener listener) {
		renderer = new PRenderer(params.imageWidth, params.imageHeight, listener);
		renderer.init();
		viewportHeight = 2.0 * Math.tan(params.fov / 2.0);
		viewportWidth = viewportHeight * ((double) params.imageWidth / (double) params.imageHeight);
		renderer.setScale(params.scale);
		recSorter = new PCopyableSorter<PHitRecord>(Comparator.comparing(PHitRecord::getT));
		update();
	}

	public void update() {
		// camera basis vectors
		v = new PVec3(0.0, 0.0, 1.0); // up 
		w = facing.neg().toVec3().unitVector(); // back
		u = PVec3.cross(v, w).unitVector(); // right
		// viewport basis vectors
		PVec2 viewportU = (u.mul(viewportWidth)).toVec2();
		pixelDeltaU = viewportU.div(params.imageWidth);
		PVec2 offset = w.add(viewportU.div(2.0)).toVec2();
		pixel0Position = position.sub(offset).add((pixelDeltaU).div(2.0));
	}

	public void cycleRenderMode() {
		final int maxRenderMode = 2;
		if (++renderMode > maxRenderMode) {
			renderMode = 0;
		}
	}

	public long draw(PSectorWorld world) {
		long startTime = System.currentTimeMillis();
		renderer.fillTile(backgroundCol, params.imageWidth, params.imageHeight, 0, 0);
		switch (renderMode) {
			case 0 -> drawWorld(world);
			case 1 -> drawFloorMask(PColour.BLUE, calculateMasks(world));
			case 2 -> drawCeilingMask(PColour.GREEN, calculateMasks(world));
			default -> { return -1; }
		}
		renderer.repaint();
		return System.currentTimeMillis() - startTime;

	}

	private void drawWorld(PSectorWorld world) {
		PFlatMask[] masks = calculateMasks(world);
		drawSectors(world, masks);
	}

	private PFlatMask[] calculateMasks(PSectorWorld world) {
		PFlatMask[] masks = new PFlatMask[world.getSectors().length];
		for (int i = 0; i < world.getSectors().length; i++) {
			masks[i] = new PFlatMask(params.imageWidth, params.imageHeight);
		}
		for (int i = 0; i < params.imageWidth; i++) {
			PRay r = getRay(i);	
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			recSorter.quicksort(hits, 0, hits.length - 1);
			// recSorter.bubblesort(hits);
			for (int j = hits.length - 1; j >= 0; j--) {
				int[] bounds = getColumnBounds(r, hits[j]);
				masks[hits[j].getSectorIndex()].saveToBoundingMasks(elevation, hits[j], i, bounds);
			}
		}
		return masks;

	}

	private void drawSectors(PSectorWorld world, PFlatMask[] masks) {
		for (int x = 0; x < params.imageWidth; x++) {
			PRay r = getRay(x);
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			recSorter.quicksort(hits, 0, hits.length - 1);
			// recSorter.bubblesort(hits);
			drawSlice(r, hits, masks, x);
		}
	}

	private void drawSlice(PRay r, PHitRecord[] hits, PFlatMask[] masks, int x) {
		PColour fCol = PColour.GREEN;
		PColour cCol = PColour.BLUE;
		for (int j = hits.length - 1; j >= 0; j--) {
			PFlatMask mask = masks[hits[j].getSectorIndex()];
			if (hits[j].isBackface()) {
				if (getFloorHeight(hits[j]) < 0.0) { // floor above view
					renderer.writeColumn(fCol, x, mask.floor[x][0], mask.floor[x][1]);
				} 
				if (getFloorHeight(hits[j]) > 0.0) { // floor below view
					renderer.writeColumn(cCol, x, mask.floor[x][0], mask.floor[x][1]);
				}
				if (getCeilingHeight(hits[j]) < 0.0) { // ceiling above view
					renderer.writeColumn(fCol, x, mask.ceiling[x][0], mask.ceiling[x][1]);
				} 
				if (getCeilingHeight(hits[j]) > 0.0) { // ceiling below view
					renderer.writeColumn(cCol, x, mask.ceiling[x][0], mask.ceiling[x][1]);
				}
			} 
			if (!hits[j].isPortal()) {
				int[] bounds = getColumnBounds(r, hits[j]);
				PColour columnColour = rayColour(r, hits[j]);
				if (columnColour != null) {
					renderer.writeColumn(columnColour, x, bounds[0], bounds[1]);
				}
			}
		}
	}

	private void drawFloorMask(PColour colour, PFlatMask[] masks) {
		for (int i = 0; i < params.imageWidth; i++) {
			for (PFlatMask mask : masks) {
				renderer.writePixel(colour, i, mask.floor[i][0]);
				renderer.writePixel(colour, i, mask.floor[i][1]);
			}
		}
	}

	private void drawCeilingMask(PColour colour, PFlatMask[] masks) {
		for (int i = 0; i < params.imageWidth; i++) {
			for (PFlatMask mask : masks) {
				renderer.writePixel(colour, i, mask.ceiling[i][0]);
				renderer.writePixel(colour, i, mask.ceiling[i][1]);
			}
		}
	}

	private PColour rayColour(PRay r, PHitRecord rec) {
		PColour colour = rec.getColour();	
		colour = colour.mul(getDepth(r, rec));
		return colour;

	}

	private double getDepth(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		double value = 1.0 - (distance / 20.0);
		return value;

	}
	private int[] getColumnBounds(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		int botCoord = (int) Math.round((params.imageHeight / distance) * getFloorHeight(rec));
		int topCoord = (int) Math.round((params.imageHeight / distance) * getCeilingHeight(rec));
		int bottom = (params.imageHeight / 2) + botCoord;
		int top = (params.imageHeight / 2) + topCoord;
		return new int[]{clamp(params.imageHeight - bottom, 0, params.imageHeight - 1), 
				clamp(params.imageHeight - top, 0, params.imageHeight - 1)};

	}

	private int clamp(int val, int lower, int upper) {
		if (val < lower) return lower;
		if (val > upper) return upper;
		return val;

	}

	private PRay getRay(int x) {
		PVec2 offsetX = pixelDeltaU.mul(x);
		PVec2 pixelPos = pixel0Position.add(offsetX);
		PVec2 rayDir = pixelPos.sub(position);
		return new PRay(position, rayDir);

	}

	private double getFloorHeight(PHitRecord hitRec) {
		return hitRec.getFloorHeight() - elevation;

	}

	private double getCeilingHeight(PHitRecord hitRec) {
		return hitRec.getCeilingHeight() - elevation;

	}
}
