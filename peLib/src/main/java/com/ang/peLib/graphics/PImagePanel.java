package com.ang.peLib.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Customized implementation of a {@link javax.swing.JPanel} that allows for 
 * writing individual pixels.
 */
public class PImagePanel extends JPanel {
	private BufferedImage image;
	private Graphics g;

	/**
	 * {@inheritDoc}
	 */
	public PImagePanel(BufferedImage image) {
		this.image = image;
	}

	/**
	 * {@inheritDoc}
	 * Overriden function allowing for writing individual pixels to the image.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		}
		this.g = g;
	}

	public Graphics getGraphics() {
		return g;

	}
}
