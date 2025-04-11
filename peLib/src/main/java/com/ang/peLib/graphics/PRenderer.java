package com.ang.peLib.graphics;

import com.ang.peLib.inputs.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import javax.swing.JFrame;

public class PRenderer {
	protected JFrame frame = new JFrame();
	protected BufferedImage img;
	protected int width;
	protected int height;
	private PImagePanel imgPanel;

	public PRenderer(int width, int height, Object listener) {
		this.width = width;
		this.height = height;
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imgPanel = new PImagePanel(img);
		init(listener);
	}

	public JFrame frame() {
		return frame;

	}

	public int getWindowWidth() {
		return frame.getWidth();

	}

	public int getWindowHeight() {
		return frame.getHeight();

	}

	public void close() {
		frame.dispose();
	}

	public void repaint() {
		frame.repaint();
	}

	private void init(Object listener) {
		imgPanel.setPreferredSize(new Dimension(width, height));
		frame.getContentPane().add(imgPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		imgPanel.setFocusable(true);
		imgPanel.requestFocusInWindow();
		if (listener instanceof PMovementInputListener) {
			PMovementInputListener il = (PMovementInputListener) listener;
			imgPanel.addKeyListener(il);
		} else if (listener instanceof PMouseInputListener) {
			PMouseInputListener mil = (PMouseInputListener) listener;
			imgPanel.addMouseMotionListener(mil);
			imgPanel.addMouseListener(mil);
			imgPanel.addMouseWheelListener(mil);
		} else {
			System.err.println("Invalid listener supplied to renderer init");
			frame.dispose();
			return;

		}
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
	}

	public void writePixel(PColour colour, int x, int y) {
		int col = processToInt(colour);
		if (!inBounds(x, y)) {
			return;

		}
		img.setRGB(x, y, col);
	}

	public void writeColumn(PColour colour, int x, int bottom, int top) {
		int columnColour = processToInt(colour);
		// fill in pixels from the top, so higher pixels have a smaller y value
		for (int y = 0; y < height; y++) {
			if ((y >= top) && (y <= bottom)) {
				if (!inBounds(x, y)) {
					continue;

				}
				img.setRGB(x, y, columnColour);
			}
		}
	}

	public void writeTile(PColour colour, int width, int height, int x, int y) {
		int tileColour = processToInt(colour);
		for (int j = y; j < y + height; j++) {
			for (int i = x; i < x + width; i++) {
				if (!inBounds(i, j)) {
					continue;

				}
				img.setRGB(i, j, tileColour);
			}
		}
	}

	protected int processToInt(PColour c) {
		// convert from linear to gamma space
		double r = c.r() > 0 ? Math.sqrt(c.r()) : 0.0;
		double g = c.g() > 0 ? Math.sqrt(c.g()) : 0.0;
		double b = c.b() > 0 ? Math.sqrt(c.b()) : 0.0;
		// normalize and round
		int rComponent = (int) Math.min(r * 255, 255);
		int gComponent = (int) Math.min(g * 255, 255);
		int bComponent = (int) Math.min(b * 255, 255);
		// convert to BufferedImage.TYPE_INT_RGB
		return (rComponent << 16) | (gComponent << 8) | (bComponent);

	}

	protected boolean inBounds(int x, int y) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return false;

		}
		return true;

	}
}
