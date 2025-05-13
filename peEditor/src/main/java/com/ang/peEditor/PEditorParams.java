package com.ang.peEditor;

import com.ang.peLib.graphics.PColour;

public class PEditorParams {
	public final double ASPECT_RATIO;
	public final int 	CORNER_SIZE;
	public final int 	CORNER_RADIUS;
	public boolean 		snapToGrid;
	public double 		scale;
	public int 			width;
	public int 			height;
	public int			historyLength;
	public PColour 		backgroundColour;
	public PColour 		gridColour;
	public PColour 		axisColour;
	public PColour 		lineColour;
	public PColour 		cornerColour;
	public PColour 		selectedColour;
	public PColour 		selectedColour2;

	public PEditorParams() {
		ASPECT_RATIO = 16.0 / 9.0;
		CORNER_SIZE = 8;
		CORNER_RADIUS = 6;
		init();
	}

	public void init() {
		snapToGrid 			= true;
		scale 				= 15.0;
		width 				= 1000;
		height 				= (int) Math.round((double) width / ASPECT_RATIO);
		backgroundColour 	= new PColour(0.1, 0.1, 0.15);
		gridColour 			= new PColour(0.15, 0.15, 0.2);
		axisColour 			= new PColour(0.6, 0.3, 0.3);
		lineColour 			= new PColour(0.6, 0.6, 0.6);
		cornerColour 		= new PColour(0.8, 0.8, 0.8);
		selectedColour		= new PColour(0.3, 0.4, 0.7);
		selectedColour2		= new PColour(0.2, 0.3, 0.9);
	}
}
