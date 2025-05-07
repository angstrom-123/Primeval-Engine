package com.ang.peEditor;

import com.ang.peLib.graphics.PColour;

public class PEditorParams {
	public final double ASPECT_RATIO 		= 16.0 / 9.0;
	public final int 	CORNER_SIZE 		= 8;
	public double 		scale;
	public int 			width;
	public int 			height;
	public int			historyLength;
	public PColour 		backgroundColour;
	public PColour 		gridColour;
	public PColour 		lineColour;
	public PColour 		cornerColour;
	public PColour 		selectedColour;

	public PEditorParams() {
		init();
	}

	public void init() {
		scale 				= 15.0;
		width 				= 1000;
		height 				= (int) Math.round((double) width / ASPECT_RATIO);
		backgroundColour 	= new PColour(0.1, 0.1, 0.15);
		gridColour 			= new PColour(0.15, 0.15, 0.2);
		lineColour 			= new PColour(0.6, 0.6, 0.6);
		cornerColour 		= new PColour(0.8, 0.8, 0.8);
		selectedColour		= new PColour(0.3, 0.4, 0.7);
	}
}
