package com.ang.peLib.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Class for registering mouse inputs such as clicks and drags.
 * All events are passed to the interface {@link PMouseInputInterface}
 * @see PMouseInputInterface
 */
public class PMouseInputListener extends PListener implements MouseMotionListener, 
	   MouseListener, MouseWheelListener {
	private final static int DEBOUNCE_MS = 50;
	private final static int SMALL_DEBOUNCE_MS = 10;
	private static long lastScrollTime = 0;
	private static long lastMoveTime = 0;
	private static long lastDragTime = 0;
	private PMouseInputInterface mouseInterface;

	/**
	 * Constructs the listener with an output interface.
	 * @param mouseInterface the {@link PMouseInputInterface} to send events to
	 * @see 				 PMouseInputInterface
	 */
	public PMouseInputListener(PMouseInputInterface mouseInterface) {
		this.mouseInterface = mouseInterface;
	}

	/**
	 * {@inheritDoc}
	 * Sends the scroll amount to the {@link PMouseInputInterface#mouseScrolled(int)} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mouseScrolled(int)
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (System.currentTimeMillis() - lastScrollTime >= DEBOUNCE_MS) {	
			lastScrollTime = System.currentTimeMillis();
			mouseInterface.mouseScrolled(e.getX(), e.getY(), e.getUnitsToScroll());
		}
	}

	/**
	 * {@inheritDoc}
	 * Sends the mouse position to the {@link PMouseInputInterface#mouseMoved(int, int)} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mouseMoved(int, int)
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (System.currentTimeMillis() - lastMoveTime >= DEBOUNCE_MS) {	
			lastMoveTime = System.currentTimeMillis();
			mouseInterface.mouseMoved(e.getX(), e.getY());
		}
	}

	/**
	 * {@inheritDoc}
	 * Sends the mouse position to {@link PMouseInputInterface#mouseDragged(int, int)} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mouseDragged(int, int)
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// if (System.currentTimeMillis() - lastDragTime >= SMALL_DEBOUNCE_MS) {	
			lastDragTime = System.currentTimeMillis();
			// only trigger on left click drag
			if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
				mouseInterface.mouseDragged(e.getX(), e.getY());
			}
		// }
	}

	/**
	 * {@inheritDoc}
	 * Sends the mouse position to {@link PMouseInputInterface#mousePressed(int, int)} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mousePressed(int, int)
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseInterface.mousePressed(e.getX(), e.getY());
		} else {
			mouseInterface.rightMousePressed(e.getX(), e.getY());
		}
	}
	
	/**
	 * {@inheritDoc}
	 * Sends the mouse position to {@link PMouseInputInterface#mouseReleased(int, int)} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mouseReleased(int, int)
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseInterface.mouseReleased(e.getX(), e.getY());
		} else {
			mouseInterface.rightMouseReleased(e.getX(), e.getY());
		}
	}

	/**
	 * {@inheritDoc}
	 * Sends the exit event to {@link PMouseInputInterface#mouseExited()} 
	 * as specified in the constructor.
	 * @see PMouseInputInterface#mouseExited()
	 * @see #PMouseInputListener(PMouseInputInterface)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		mouseInterface.mouseExited();
	}
	
	/**
	 * {@inheritDoc}
	 * Currently not implemented in the listener.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	/**
	 * {@inheritDoc}
	 * Currently not implemented in the listener.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
}
