package com.ang.peLib.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public class PMouseInputListener implements MouseMotionListener, MouseListener, 
	   MouseWheelListener {
	private PMouseInputInterface mouseInterface;

	public PMouseInputListener(PMouseInputInterface mouseInterface) {
		this.mouseInterface = mouseInterface;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseInterface.mouseScrolled(e.getUnitsToScroll());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseInterface.mouseMoved(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseInterface.mouseDragged(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseInterface.mousePressed(e.getX(), e.getY());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseInterface.mouseReleased(e.getX(), e.getY());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseInterface.mouseExited();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
}
