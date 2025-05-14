package com.ang.peCore;

import com.ang.peLib.threads.PUpdateWorker;
import com.ang.peLib.graphics.*;
import com.ang.peLib.maths.*;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.PMovementInputListener;

public class PCamera {
	private PColour 	backgroundCol 	= new PColour(0.3, 0.4, 0.6);
	private int 		imageWidth 		= 100;
	private double 		aspectRatio 	= 16.0 / 9.0;
	private double 		fov 			= Math.PI / 3.0;
	private PVec2 		position 		= new PVec2(0.0, 0.0);
	private PVec2 		facing 			= new PVec2(0.0, 1.0);
	private double 		viewportHeight;
	private double 		viewportWidth;
	private int 		imageHeight;
	private PVec3 		w, u, v;
	private PVec2 		pixel0Position;
	private PVec2 		pixelDeltaU;
	private PRenderer 	renderer;
	
	public PCamera(int imageWidth) {
		this.imageWidth = imageWidth;
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

	public void init(PMovementInputListener listener) {
		imageHeight = (int) Math.round((double) imageWidth / aspectRatio);
		imageWidth = Math.max(imageWidth, 1);
		renderer = new PRenderer(imageWidth, imageHeight, listener);
		renderer.init();
		viewportHeight = 2.0 * Math.tan(fov / 2.0);
		viewportWidth = viewportHeight * ((double) imageWidth / (double) imageHeight);
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
		// Clearing the previous frame
		renderer.fillTile(backgroundCol, imageWidth, imageHeight, 0, 0);
		// Drawing the world
		drawWalls(world);
		// Displaying buffer on screen
		renderer.repaint();
		return System.currentTimeMillis() - startTime;

	}

	private void drawWalls(PSectorWorld world) {
		for (int i = 0; i < imageWidth; i++) {
			PRay r = getRay(i);	
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			sortDepth(hits);
			for (int j = hits.length - 1; j >= 0; j--) {
				PHitRecord hitRec = hits[j];
				int[] bounds = getColumnBounds(r, hitRec);
				renderer.writeColumn(rayColour(r, hitRec), i, 
						bounds[0], bounds[1]);
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
		double value = 1.0 - (distance / 30.0);
		return value;

	}

	private int[] getColumnBounds(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		int botScreenPos = (int) Math.round(
				(imageHeight / distance) * rec.getFloorHeight());
		int topScreenPos = (int) Math.round(
				(imageHeight / distance) * rec.getCeilingHeight());
		int bottom = Math.max((imageHeight / 2) + botScreenPos, 0);
		int top = Math.min((imageHeight / 2) + topScreenPos, imageHeight - 1);
		return new int[]{imageHeight - bottom, imageHeight - top};

	}

	private PRay getRay(int x) {
		PVec2 offsetX = pixelDeltaU.mul(x);
		PVec2 pixelPos = pixel0Position.add(offsetX);
		PVec2 rayDir = pixelPos.sub(position);
		return new PRay(position, rayDir);

	}

	private void sortDepth(PHitRecord[] hits) {
		for (int i = hits.length - 1; i >= 0; i--) {
			for (int j = 1; j <= i; j++) {
				if (hits[j - 1].getT() > hits[j].getT()) {
					PHitRecord temp = hits[j - 1].copy();
					hits[j - 1] = hits[j].copy();
					hits[j] = temp;
				}
			}
		}
	}
}
