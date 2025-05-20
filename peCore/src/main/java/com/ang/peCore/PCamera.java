package com.ang.peCore;

import com.ang.peLib.graphics.*;
import com.ang.peLib.maths.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.PMovementInputListener;
import com.ang.peLib.inputs.PFullKeyboardInputInterface;
import com.ang.peLib.inputs.PFullKeyboardInputListener;

public class PCamera {
	private long 		lastCycleMs		= 0;
	private int 		renderMode 		= 0;
	private PColour 	backgroundCol 	= new PColour(0.3, 0.4, 0.6);
	private int 		imageWidth 		= 100;
	private double 		aspectRatio 	= 16.0 / 9.0;
	private double 		scale 			= PGameParams.scale;
	private double 		fov 			= Math.PI / 4.0;
	private double 		elevation		= 0.0;
	private PVec2 		position 		= new PVec2(0.0, 0.0);
	private PVec2 		facing 			= new PVec2(0.0, 1.0);
	private double 		viewportHeight;
	private double 		viewportWidth;
	private int 		imageHeight;
	private PVec3 		w, u, v;
	private PVec2 		pixel0Position;
	private PVec2 		pixelDeltaU;
	private PRenderer 	renderer;
	private boolean[][] mask;
	
	public PCamera(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public PRenderer getRenderer() {
		return renderer;

	}

	public void cycleRenderMode(long currentTimeMs) {
		final int maxRenderMode = 1;
		final long debounceMs = 200;
		if (currentTimeMs - lastCycleMs > debounceMs) {
			if (++renderMode > maxRenderMode) renderMode = 0;
			lastCycleMs = currentTimeMs;
		}
	}

	public void setTransform(PVec2 position, PVec2 facing) {
		this.position = position;
		this.facing = facing;
		this.position = new PVec2(-20.0, 0.0);
		this.facing = (new PVec2(1.0, 1.0)).unitVector();
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

	public void init(PFullKeyboardInputListener listener) {
		imageHeight = (int) Math.round((double) imageWidth / aspectRatio);
		imageWidth = Math.max(imageWidth, 1);
		renderer = new PRenderer(imageWidth, imageHeight, listener);
		renderer.init();
		viewportHeight = 2.0 * Math.tan(fov / 2.0);
		viewportWidth = viewportHeight * ((double) imageWidth / (double) imageHeight);
		renderer.setScale(scale);
		update();
	}

	public void update() {
		// camera basis vectors
		v = new PVec3(0.0, 0.0, 1.0); // up 
		w = facing.neg().toVec3().unitVector(); // back
		u = PVec3.cross(v, w).unitVector(); // right
		// viewport basis vectors
		PVec2 viewportU = (u.mul(viewportWidth)).toVec2();
		pixelDeltaU = viewportU.div(imageWidth);
		PVec2 offset = w.add(viewportU.div(2.0)).toVec2();
		pixel0Position = position.sub(offset).add((pixelDeltaU).div(2.0));
	}

	public long draw(PSectorWorld world) {
		long startTime = System.currentTimeMillis();
		renderer.fillTile(backgroundCol, imageWidth, imageHeight, 0, 0);
		createMask(world);
		switch (renderMode) {
		case 0:
			drawWalls(world);
			break;

		case 1:
			drawMask(PColour.BLUE, mask);
			break;

		default:
			break;

		}
		renderer.repaint();
		return System.currentTimeMillis() - startTime;

	}

	private void drawMask(PColour colour, boolean[][] mask) {
		renderer.fillTile(PColour.WHITE, imageWidth, imageHeight, 0, 0);
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				if (mask[x][y]) renderer.writePixel(colour, x, y);
			}
		}
	}
	private void createMask(PSectorWorld world) {
		mask = new boolean[imageWidth][imageHeight];
		for (int i = 0; i < imageWidth; i++) {
			PRay r = getRay(i);	
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			for (int j = hits.length -1 ; j >= 0; j--) { // draw back to front
				PHitRecord hitRec = hits[j];
				int[] bounds = getColumnBounds(r, hitRec);
				for (int k = bounds[1]; k < bounds[0]; k++) {
					mask[i][k] = true;
				}
			}
		}
	}

	private void drawWalls(PSectorWorld world) {
		for (int i = 0; i < imageWidth; i++) {
			PRay r = getRay(i);	
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			sort(hits, 0, hits.length - 1);
			for (int j = hits.length - 1; j >= 0; j--) { // draw back to front
				PHitRecord hitRec = hits[j];
				int[] bounds = getColumnBounds(r, hitRec);
				if (hitRec.isBackface()) {
					// renderer.writeColumn(rayColour(r, hitRec), i, bounds[0], bounds[1]);
					if (hitRec.getFloorHeight() > elevation) { // draw floor
						renderer.writeColumn(PColour.BLUE, i, bounds[0], 0);
					} else if (hitRec.getCeilingHeight() < elevation) { // draw ceiling
						renderer.writeColumn(PColour.GREEN, i, imageHeight - 1, bounds[1]);
					}
				} else {
					renderer.writeColumn(rayColour(r, hitRec), i, bounds[0], bounds[1]);
				}
			}
		}
	}

	private PColour rayColour(PRay r, PHitRecord rec) {
		PColour colour = rec.getColour();	
		if (rec.isBackface()) {
			colour = new PColour(1.0, 0.0, 0.0);
		}
		colour = colour.mul(getDepth(r, rec));
		return colour;

	}

	private double getDepth(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		double value = 1.0 - (distance / 50.0);
		return value;

	}
	private int[] getColumnBounds(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		int botScreenPos = (int) Math.round((imageHeight / distance) * rec.getFloorHeight());
		int topScreenPos = (int) Math.round((imageHeight / distance) * rec.getCeilingHeight());
		int bottom = Math.max((imageHeight / 2) + botScreenPos, 0);
		int top = Math.min((imageHeight / 2) + topScreenPos, imageHeight);
		return new int[]{imageHeight - bottom, imageHeight - top};

	}

	private PRay getRay(int x) {
		PVec2 offsetX = pixelDeltaU.mul(x);
		PVec2 pixelPos = pixel0Position.add(offsetX);
		PVec2 rayDir = pixelPos.sub(position);
		return new PRay(position, rayDir);

	}

	private void sort(PHitRecord[] hits, int left, int right) {
		if (left < right) {
			int mid = partition(hits, left, right);
			sort(hits, left, mid - 1);
			sort(hits, mid + 1, right);
		}
	}

	private int partition(PHitRecord[] hits, int left, int right) {
		double pivot = hits[right].getT();
		int i = left;
		for (int j = left; j < right; j++) {
			if (hits[j].getT() <= pivot) {
				swap(hits, i, j);
				i++;
			}
		}
		swap(hits, i, right);
		return i;

	}

	private void swap(PHitRecord[] hits, int index0, int index1) {
		PHitRecord temp = hits[index0].copy();
		hits[index0] = hits[index1];
		hits[index1] = temp;
	}
}
