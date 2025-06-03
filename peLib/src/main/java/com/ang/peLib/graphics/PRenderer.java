package com.ang.peLib.graphics;

import com.ang.peLib.inputs.*;
import com.ang.peLib.threads.PUpdateWorker;

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
	private PUpdateWorker[] workersToKill = new PUpdateWorker[0];
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
	 * Constructs the renderer with a listener for all keyboard inputs.
	 * This renderer tracks all keyboard inputs allowing for shortcuts.
	 * @param width    the width of the window to create and render to
	 * @param height   the height of the window to create and render to
	 * @param listener the keyboard listener to use
	 * @see   		   com.ang.peLib.inputs.PMouseInputListener
	 */
	public PRenderer(int width, int height, PFullKeyboardInputListener listener) {
		this.width = width;
		this.height = height;
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imgPanel = new PImagePanel(img);
		this.listener = listener;
	}

	/**
	 * Adds a worker to the list of processes to kill when closing the window.
	 * @param worker the worker to be killed when closing the window
	 */
	public void terminateOnClose(PUpdateWorker worker) {
		PUpdateWorker[] temp = new PUpdateWorker[workersToKill.length + 1];
		for (int i = 0; i < workersToKill.length; i++) {
			temp[i] = workersToKill[i];
		}
		temp[temp.length - 1] = worker;
		workersToKill = temp;
	}

	/**
	 * Writes a string to the title bar of the window.
	 * @param text the text to write to the title bar
	 */
	public void writeToTitleBar(String text) {
		frame.setTitle(text);
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
		for (int i = 0; i < workersToKill.length; i++) {
			workersToKill[i].doStop();
		}
		frame.dispose();
	}

	/**
	 * Refreshes the window.
	 */
	public void repaint() {
		frame.repaint();
	}

	/**
	 * Applies a multiplier to the size of each pixel.
	 * @param multiplier the multiplier to apply
	 */
	public void setScale(double multiplier) {
		frame.setSize((int) Math.round(imgPanel.getWidth() * multiplier), 
				(int) Math.round(imgPanel.getHeight() * multiplier));
		frame.setLocationRelativeTo(null);
		frame.repaint();
	}

	/**
	 * Initializes the renderer with an input listener.
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
		} else if (listener instanceof PFullKeyboardInputListener) {
			PFullKeyboardInputListener il = (PFullKeyboardInputListener) listener;
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
				close();
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
		for (int y = top; y <= bottom; y++) {
			if (inBounds(x, y)) {
				img.setRGB(x, y, columnColour);
			}
		}
	}

	/**
	 * Writes a line between 2 screenspace coordinates.
	 * @param colour the colour the draw the line in
	 * @param x0 	 x screenspace coordinate to start the line at
	 * @param y0 	 y screenspace coordinate to start the line at
	 * @param x1 	 x screenspace coordinate to end the line at
	 * @param y1 	 y screenspace coordinate to end the line at
	 */
	public void writeLine(PColour colour, int x0, int y0, int x1, int y1) {
		writeLine(colour, x0, y0, x1, y1, Integer.MAX_VALUE);
	}

	/**
	 * Writes a dotted line between 2 screenspace coordinates.
	 * @param colour  the colour the draw the line in
	 * @param x0 	  x screenspace coordinate to start the line at
	 * @param y0 	  y screenspace coordinate to start the line at
	 * @param x1 	  x screenspace coordinate to end the line at
	 * @param y1 	  y screenspace coordinate to end the line at
	 * @param dotrate amount of consecutive pixels between gaps
	 */
	public void writeLine(PColour colour, int x0, int y0, int x1, int y1, int dotrate) {
		int lineColour = processToInt(colour);
		if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
			if (x0 > x1) writeLineLow(lineColour, x1, y1, x0, y0, dotrate);
			else writeLineLow(lineColour, x0, y0, x1, y1, dotrate);
		} else {
			if (y0 > y1) writeLineHigh(lineColour, x1, y1, x0, y0, dotrate);
			else writeLineHigh(lineColour, x0, y0, x1, y1, dotrate);
		}
	}

	/**
	 * Writes a line down from the starting coordinates to the ending coordinates.
	 * @see #writeLine(PColour, int, int, int, int, int)
	 */
	private void writeLineLow(int lineColour, int x0, int y0, int x1, int y1, int dotrate) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int yIncrement = (dy < 0) ? -1 : 1;
		dy *= yIncrement;
		int error = 2 * dy - dx;
		int y = y0;
		boolean doDraw = true;
		int counter = 0;
		for (int x = x0; x < x1; x++) {
			if (doDraw && inBounds(x, y)) img.setRGB(x, y, lineColour);
			if (error > 0) {
				y += yIncrement;
				error += 2 * (dy - dx);
			} else error += 2 * dy;
			if (counter >= dotrate) {
				doDraw = !doDraw;
				counter = 0;
			} 
			counter++;
		}
	}

	/**
	 * Writes a line up from the starting coordinates to the ending coordinates.
	 * @see #writeLine(PColour, int, int, int, int, int)
	 */
	private void writeLineHigh(int lineColour, int x0, int y0, int x1, int y1, int dotrate) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int xIncrement = (dx < 0) ? -1 : 1;
		dx *= xIncrement;
		int error = 2 * dx - dy;
		int x = x0;
		boolean doDraw = true;
		int counter = 0;
		for (int y = y0; y < y1; y++) {
			if (doDraw && inBounds(x, y)) img.setRGB(x, y, lineColour);
			if (error > 0) {
				x += xIncrement;
				error += 2 * (dx - dy);
			} else error += 2 * dx;
			if (counter >= dotrate) {
				doDraw = !doDraw;
				counter = 0;
			} 
			counter++;
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
	 * Writes text at the specified coordinates.
	 * @param text the text to write 
	 * @param x    x screenspace coordinate to write the text at
	 * @param y    y screenspace coordinate to write the text at
	 */
	public void writeText(String text, int x, int y) {
		img.getGraphics().drawString(text, x, y);
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

	/**
	 * Checks if a given screenspace coordinate is within the bounds of the screen.
	 * @param  x the x coordinate to check
	 * @param  y the y coordinate to check
	 * @return {@code true} if the point is in bounds, else {@code false}
	 */
	protected boolean inBounds(int x, int y) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return false;

		}
		return true;

	}
}
