package com.ang.peCore;

import java.util.Comparator;

import com.ang.peLib.graphics.*;
import com.ang.peLib.maths.*;
import com.ang.peLib.utils.PCopyableSorter;
import com.ang.peLib.hittables.*;
import com.ang.peLib.inputs.PFullKeyboardInputListener;

/**
 * Handles rendering the player's perspective.
 */
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
	
	/**
	 * Constructs a new camera.
	 * @param params params for the game
	 */
	public PCamera(PGameParams params) {
		this.params = params;
	}

	/**
	 * Returns the renderer used by this camera.
	 * @return the renderer used by this camera
	 */
	public PRenderer getRenderer() {
		return renderer;

	}

	/**
	 * Sets the position and facing direction of the camera.
	 * Facing should be a unit vector
	 * @param position the new position of the camera
	 * @param facing   the new facing vector for the camera
	 */
	public void setTransform(PVec2 position, PVec2 facing) {
		this.position = position;
		this.facing = facing;
	}

	/**
	 * Changes the camera's position by an offset.
	 * @param positionDelta offset to apply to the camera's position
	 */
	public void changePosition(PVec2 positionDelta) {
		PVec2 xDelta = u.toVec2().neg().mul(positionDelta.x());
		PVec2 yDelta = facing.mul(positionDelta.y());
		position = position.add(xDelta).add(yDelta);
	}

	/**
	 * Changes the camera's facing vector by an angle offset.
	 * @param theta angle to add to the camera's facing vector angle (radians)
	 */
	public void changeFacing(double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double xPos = facing.x() * cosTheta - facing.y() * sinTheta;
		double yPos = facing.x() * sinTheta + facing.y() * cosTheta;
		facing = new PVec2(xPos, yPos).unitVector();
	}

	/**
	 * Changes the camera's elevation by an offset.
	 * @param delta offset to apply to the camera's position
	 */
	public void changeElevation(double delta) {
		elevation += delta;
		update();
	}

	/**
	 * Initializes the camera and required objects.
	 * @param listener keyboard listener to use 
	 * @see 		   com.ang.peLib.inputs.PFullKeyboardInputListener
	 */
	public void init(PFullKeyboardInputListener listener) {
		renderer = new PRenderer(params.imageWidth, params.imageHeight, listener);
		renderer.init();
		viewportHeight = 2.0 * Math.tan(params.fov / 2.0);
		viewportWidth = viewportHeight * ((double) params.imageWidth / (double) params.imageHeight);
		renderer.setScale(params.scale);
		recSorter = new PCopyableSorter<PHitRecord>(Comparator.comparing(PHitRecord::getT));
		update();
	}

	/**
	 * Updates camera basis vectors used for rendering.
	 * This should be called after changing the camera's transform
	 */
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

	/**
	 * Cycles the rendering mode of the camera.
	 */
	public void cycleRenderMode() {
		final int maxRenderMode = 2;
		if (++renderMode > maxRenderMode) {
			renderMode = 0;
		}
	}

	/**
	 * Renders the world from the camera's perspective in the current render mode.
	 * Can render in 3 modes. Default is regular mode, mode 2 draws the floor 
	 * mask, mode 3 draws the ceiling mask.
	 * @param  world the world to draw 
	 * @return 		 the time in ms taken to render the frame
	 * @see  		 com.ang.peLib.hittables.PSectorWorld
	 */
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

	/**
	 * Renders the world from the camera's perspective in normal mode.
	 * @param world the world to draw 
	 * @see  		com.ang.peLib.hittables.PSectorWorld
	 */
	private void drawWorld(PSectorWorld world) {
		PFlatMask[] masks = calculateMasks(world);
		drawSectors(world, masks);
	}

	/**
	 * Calculates the floor and ceiling masks for each sector in the world.
	 * @param  world the world to calculate the masks for
	 * @return 		 an array of flat masks corresponding to each sector in the 
	 * 				 world storing the floor and ceiling height for each
	 * @see 		 com.ang.peLib.graphics.PFlatMask
	 * @see 		com.ang.peLib.hittables.PSectorWorld
	 */
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

	/**
	 * Draws all of the sectors in the world.
	 * @param world the world to draw 
	 * @param masks flatmasks for this frame
	 * @see 		com.ang.peLib.hittables.PSectorWorld
	 * @see 	  	com.ang.peLib.graphics.PFlatMask
	 */
	private void drawSectors(PSectorWorld world, PFlatMask[] masks) {
		for (int x = 0; x < params.imageWidth; x++) {
			PRay r = getRay(x);
			PHitRecord[] hits = world.allHits(r, PInterval.universe());
			recSorter.quicksort(hits, 0, hits.length - 1);
			// recSorter.bubblesort(hits);
			drawSlice(r, hits, masks, x);
		}
	}

	/**
	 * Draws a vertical slice of the world at a given x coordinate.
	 * Renders walls, floors, and ceilings.
	 * Floors and ceilings are rendered with flat colours currently, the walls 
	 * are rendered with their distance from the camera visualized.
	 * @param r 	ray that was cast through the world for this x coordinate 
	 * @param hits  array of hitrecords from each intersection that the ray had 
	 * 				with the world
	 * @param masks flatmasks for this frame
	 * @param x 	the screen space x coordinate for this slice 
	 * @see 	  	com.ang.peLib.maths.PRay 
	 * @see 	  	com.ang.peLib.hittables.PHitRecord 
	 * @see 	  	com.ang.peLib.graphics.PColour
	 * @see 	  	com.ang.peLib.graphics.PFlatMask
	 */
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

	/**
	 * Draws all floor mask, used in render mode 2.
	 * @param colour the colour to render the mask in 
	 * @param masks  the flat masks to draw
	 * @see 	  	 com.ang.peLib.graphics.PColour
	 * @see 	  	 com.ang.peLib.graphics.PFlatMask
	 */
	private void drawFloorMask(PColour colour, PFlatMask[] masks) {
		for (int i = 0; i < params.imageWidth; i++) {
			for (PFlatMask mask : masks) {
				renderer.writePixel(colour, i, mask.floor[i][0]);
				renderer.writePixel(colour, i, mask.floor[i][1]);
			}
		}
	}

	/**
	 * Draws all ceiling mask, used in render mode 3.
	 * @param colour the colour to render the mask in 
	 * @param masks  the flat masks to draw
	 * @see 	  	 com.ang.peLib.graphics.PColour
	 * @see 	  	 com.ang.peLib.graphics.PFlatMask
	 */
	private void drawCeilingMask(PColour colour, PFlatMask[] masks) {
		for (int i = 0; i < params.imageWidth; i++) {
			for (PFlatMask mask : masks) {
				renderer.writePixel(colour, i, mask.ceiling[i][0]);
				renderer.writePixel(colour, i, mask.ceiling[i][1]);
			}
		}
	}

	/**
	 * Determines the colour of a ray that was cast through the world.
	 * This is currently only used when drawing walls to display the 
	 * depth from the camera.
	 * @param  r   the ray to get the colour for 
	 * @param  rec the hitrecord recording the intersection that this ray had 
	 * 			   with the world
	 * @return 	   a colour for the ray
	 * @see 	   com.ang.peLib.hittables.PHitRecord 
	 * @see 	   com.ang.peLib.maths.PRay 
	 * @see 	   com.ang.peLib.graphics.PColour
	 */
	private PColour rayColour(PRay r, PHitRecord rec) {
		PColour colour = rec.getColour();	
		colour = colour.mul(getDepth(r, rec));
		return colour;

	}

	/**
	 * Calculates a scaled depth value for a ray's intersection with the world.
	 * @param  r   the ray to get the depth for
	 * @param  rec the hitrecord recording the intersection that this ray had 
	 * 			   with the world
	 * @return 	   the depth value for this ray
	 * @see 	   com.ang.peLib.maths.PRay
	 * @see 	   com.ang.peLib.hittables.PHitRecord
	 */
	private double getDepth(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		double value = 1.0 - (distance / 20.0);
		return value;

	}
	/**
	 * Calculates the top and bottom screen space coordinates for a wall based 
	 * on its distance to the camera.
	 * @param  r   the ray to get the bounds for
	 * @param  rec the hitrecord recording the intersection that this ray had 
	 * 			   with the world
	 * @return 	   a pair of values for the y pixel coordinate of the bottom of 
	 * 			   the wall, and the y pixel coordinate of the top of the wall
	 * @see 	   com.ang.peLib.maths.PRay
	 * @see 	   com.ang.peLib.hittables.PHitRecord
	 */
	private int[] getColumnBounds(PRay r, PHitRecord rec) {
		double distance = (r.at(rec.getT()).sub(r.getOrigin())).length();
		int botCoord = (int) Math.round((params.imageHeight / distance) * getFloorHeight(rec));
		int topCoord = (int) Math.round((params.imageHeight / distance) * getCeilingHeight(rec));
		int bottom = (params.imageHeight / 2) + botCoord;
		int top = (params.imageHeight / 2) + topCoord;
		return new int[]{clamp(params.imageHeight - bottom, 0, params.imageHeight - 1), 
				clamp(params.imageHeight - top, 0, params.imageHeight - 1)};

	}

	/**
	 * Clamps a value within a range.
	 * @param  val   the value to clamp 
	 * @param  lower the lower bound for the value
	 * @param  upper the upper bound for the value
	 * @return 		 the clamped value
	 */
	private int clamp(int val, int lower, int upper) {
		if (val < lower) return lower;
		if (val > upper) return upper;
		return val;

	}

	/**
	 * Returns a ray with perspective projection for a given x screen space coordinate.
	 * @param  x the x pixel coordinate for which to get the ray for
	 * @return 	 a ray in the direction of the x pixel coordinate specified with 
	 * 			 perspective projection
	 * @see 	 com.ang.peLib.maths.PRay
	 * @see 	 com.ang.peLib.maths.PVec2
	 */
	private PRay getRay(int x) {
		PVec2 offsetX = pixelDeltaU.mul(x);
		PVec2 pixelPos = pixel0Position.add(offsetX);
		PVec2 rayDir = pixelPos.sub(position);
		return new PRay(position, rayDir);

	}

	/**
	 * Returns the floor height of a hitrecord based on current elevation.
	 * @param  hitRec the hitrecord to get the floor height for 
	 * @return 		  relative floor height based on camera elevation
	 * @see 		  com.ang.peLib.hittables.PHitRecord
	 */
	private double getFloorHeight(PHitRecord hitRec) {
		return hitRec.getFloorHeight() - elevation;

	}

	/**
	 * Returns the ceiling height of a hitrecord based on current elevation.
	 * @param  hitRec the hitrecord to get the ceiling height for 
	 * @return 		  relative ceiling height based on camera elevation
	 * @see 		  com.ang.peLib.hittables.PHitRecord
	 */
	private double getCeilingHeight(PHitRecord hitRec) {
		return hitRec.getCeilingHeight() - elevation;

	}
}
