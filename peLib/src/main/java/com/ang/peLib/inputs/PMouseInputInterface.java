package com.ang.peLib.inputs;

public interface PMouseInputInterface {
	void mouseScrolled(int units);
	void mouseMoved(int x, int y);
	void mouseDragged(int x, int y);
	void mousePressed(int x, int y);
	void mouseReleased(int x, int y);
	void mouseExited();
}
