package com.ang.peEditor.gui.menu.rmbMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.ang.peEditor.PEditorParams;

/**
 * Class for displaying options in a right click menu for corners.
 */
public class PRMBPanel extends JPanel {
	private final PEditorParams params;
	private final int CONTAINER_HEIGHT = 25;
	private final int WIDTH = 150;
	private PRMBPanelListener listener;
	private int sectorIndex;
	private int cornerIndex;
	private int height;

	/**
	 * Constructs a new right click menu.
	 * @param params 	  editor params to use for colours
	 * @param sectorIndex index of the sector that the panel is for 
	 * @param cornerIndex index of the corner that the panel is for 
	 * @param listener    listener to send events to
	 */
	public PRMBPanel(PEditorParams params, int sectorIndex, int cornerIndex, 
			PRMBPanelListener listener) {
		super();
		this.params = params;
		this.sectorIndex = sectorIndex;
		this.cornerIndex = cornerIndex;
		this.listener = listener;
		init();
	}

	/**
	 * Initializes the right click menu with all of its buttons.
	 */
	private void init() {
		JButton[] buttons = initButtons();
		height = (buttons.length * CONTAINER_HEIGHT);
		Dimension d = new Dimension(WIDTH, height);
		setSize(d);
		setPreferredSize(d);
		setLayout(null);
		for (JButton button : buttons) {
			add(button);
		}
		setVisible(true);
		revalidate();
		repaint();
	}

	/**
	 * Initializes all od the buttons for the menu.
	 * @return array of buttons for each event in the right click menu that 
	 * 		   send events to the listener
	 */
	private JButton[] initButtons() {
		String[] names = PRMBPanelActionType.getActions();
		JButton[] buttons = new JButton[names.length];
		for (int i = 0; i < names.length; i++) {
			final int x = i; // stops compiler complaining about i not being final
			JButton button = new JButton(names[i]);
			button.setBounds(0, i * CONTAINER_HEIGHT, WIDTH, CONTAINER_HEIGHT);
			button.setBackground((i % 2 == 0) ? params.guiBgColourDark : params.guiBgColour);
			button.setForeground(Color.WHITE);
			button.setActionCommand(names[i]);
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					int xFromOrigin = e.getX();
					int yFromOrigin = e.getY() + x * CONTAINER_HEIGHT;
					if ((xFromOrigin <= 0) || (xFromOrigin >= WIDTH) 
							|| (yFromOrigin <= 0) || (yFromOrigin >= height)) {
						listener.rmbMouseExit();
					}
				}
			});
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listener.rmbActionPerformed(cornerIndex, sectorIndex, e.getActionCommand());
				}
			});
			buttons[i] = button;
		}
		return buttons;

	}

	/**
	 * Overrides the preferred size.
	 * @return a dimension for the width and height of the right click menu panel
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, height);

	}
}
