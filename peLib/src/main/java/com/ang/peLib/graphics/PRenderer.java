package com.ang.peLib.graphics;

import com.ang.peLib.inputs.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Provides methods for displaying pixels to the screen.
 * All functions that write pixels to the screen treat coordinates as originating 
 * from (0, 0) at the bottom left. (except for {@link #writePixel(PColour, int, int)} 
 * which treats (0, 0) as the top left)
 */
public class PRenderer {
	protected JFrame frame = new JFrame();
	protected BufferedImage img;
	protected int width;
	protected int height;
	protected JPanel imgPanel;
	protected PListener listener;

	/**
	 * Constructs the renderer with a listener for keyboard inputs.
	 * This renderer should be used to renderer the game as it allows for 
	 * movement inputs. The supplied width and height are not definitive, this is 
	 * the target dimension of the window. The window is still resizable.
	 * @param width    the width of the window to create and render to
	 * @param height   the height of the window to create and render to
	 * @param listener the movement listener to use
	 * @see   		   com.ang.peLib.inputs.PMovementInputListener
	 */
	public PRenderer(int width, int height, PMovementInputListener listener) {
		this.width = width;
		this.height = height;
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imgPanel = new PImagePanel(img);
		this.listener = listener;
	}

	/**
	 * Constructs the renderer with a listener for mouse inputs.
	 * This renderer should be used to render the editor window as it allows for 
	 * mouse inputs that are not used while playing the game. The supplied width 
	 * and height are not definitive, this is the target dimension of the window.
	 * The window is still resizable.
	 * @param width    the width of the window to create and render to
	 * @param height   the height of the window to create and render to
	 * @param listener the mouse listener to use
	 * @see   		   com.ang.peLib.inputs.PMouseInputListener
	 */
	public PRenderer(int width, int height, PMouseInputListener listener) {
		this.width = width;
		this.height = height;
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imgPanel = new PImagePanel(img);
		this.listener = listener;
	}

	/**
	 * Returns the width of the {@link javax.swing.JFrame} used to render the screen.
	 * @return the width of the JFrame
	 * @see    javax.swing.JFrame
	 */
	public int getWindowWidth() {
		return frame.getWidth();

	}

	/**
	 * Returns the height of the {@link javax.swing.JFrame} used to render the screen.
	 * @return the height of the JFrame
	 * @see    javax.swing.JFrame
	 */
	public int getWindowHeight() {
		return frame.getHeight();

	}

	/**
	 * Closes the window.
	 */
	public void close() {
		frame.dispose();
	}

	/**
	 * Refreshes the window.
	 */
	public void repaint() {
		frame.repaint();
	}

	/**
	 * Initializes the renderer with an input listener.
	 * @param listener the {@link com.ang.peLib.inputs.PMouseInputListener} or 
	 * 				   the {@link com.ang.peLib.inputs.PMovementInputListener}
	 * 				   to attach to the window. Can be {@code null} if no 
	 * 				   listener should be attached 
	 * @see 		   com.ang.peLib.inputs.PMovementInputListener
	 * @see 		   com.ang.peLib.inputs.PMovementInputListener
	 */
	public void init() {
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
		}
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
	}

	/**
	 * Writes a pixel to specified screenspace coordinates.
	 * The colour is treated as if it is in linear colour space. It is 
	 * automatically gamma-corrected upon caling this function to map it to 
	 * gamme space. This function does not refresh the window. Automatic bounds 
	 * checks are performed on the pixel coordinates supplied.
	 * @param colour colour of the pixel to write
	 * @param x      the x coordinate (in pixels) to write the pixel at
	 * @param y      the y coordinate (in pixels) to write the pixel at
	 * @see   		 PColour
	 */
	public void writePixel(PColour colour, int x, int y) {
		int col = processToInt(colour);
		if (!inBounds(x, y)) {
			return;

		}
		img.setRGB(x, y, col);
	}

	/**
	 * Writes a vertical column of pixels at a specified position.
	 * The colour is treated as if it is in linear colour space. It is 
	 * automatically gamma-corrected upon caling this function to map it to 
	 * gamme space. This function does not refresh the window. Automatic bounds 
	 * checks are performed on the pixel coordinates supplied.
	 * @param colour colour of the pixel column to write
	 * @param x		 the x coordinate (in pixels) to write the column at
	 * @param bottom the height above the bottom of the screen (in pixels) to 
	 * 				 start the column
	 * @param top    the height above the bottom of the screen (in pixels) to 
	 * 				 end the column
	 * @see   		 PColour
	 */
	public void writeColumn(PColour colour, int x, int bottom, int top) {
		int columnColour = processToInt(colour);
		// image panel pixel indexing starts in the top left so it is filled backwards
		for (int y = 0; y < height; y++) {
			if ((y >= top) && (y <= bottom)) {
				if (!inBounds(x, y)) {
					continue;

				}
				img.setRGB(x, y, columnColour);
			}
		}
	}

	/**
	 * Writes a rectangle of pixels at a specified position.
	 * The colour is treated as if it is in linear colour space. It is 
	 * automatically gamma-corrected upon caling this function to map it to 
	 * gamme space. This function does not refresh the window. Automatic bounds 
	 * checks are performed on the pixel coordinates supplied.
	 * @param colour colour of the pixel column to write
	 * @param width  the width (in pixels) of the tile to write 
	 * @param height the height (in pixels) of the tile to write
	 * @param x		 the x coordinate (in pixels) of the top left of the tile
	 * @param y		 the y coordinate (in pixels) of the top left of the tile
	 * @see   		 PColour
	 */
	public void fillTile(PColour colour, int width, int height, int x, int y) {
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

	/**
	 * Converts a colour from linear colour space to gamme space.
	 * @param c the colour to convert
	 * @see   PColour
	 */
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
