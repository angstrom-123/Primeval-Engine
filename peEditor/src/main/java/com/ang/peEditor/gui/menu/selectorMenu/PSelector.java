package com.ang.peEditor.gui.menu.selectorMenu;

import com.ang.peEditor.PEditorParams;
import com.ang.peLib.exceptions.*;
import com.ang.peLib.files.PFileReader;
import com.ang.peLib.resources.PModuleName;
import com.ang.peLib.resources.PResourceType;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.BorderLayout;

/**
 * Class for displaying a custom file selector withing a specified directory.
 */
public class PSelector {
	private final PEditorParams params;
	private final int			WIDTH 					= 400;
	private final int			HEIGHT 					= 300;
	private final int			BUTTON_WIDTH 			= WIDTH;
	private final int			BUTTON_HEIGHT 			= 25;
	private final int			FILE_CONTAINER_HEIGHT 	= HEIGHT - BUTTON_HEIGHT;
	private JFrame				frame;
	private JPanel				filesContainer;
	private JScrollPane			scrollPane;
	private JTextField			textField;
	private JPanel				inputContainer;
	private JButton				submitButton;
	private String				selection;
	private PSelectorType 		type;
	private JFrame 				parent;
	private PSelectorListener 	listener;

	/**
	 * Constructs a new selector.
	 * @param params   editor params used for colours 
	 * @param type     selector type (open or save)
	 * @param parent   jframe that this selector is tied to 
	 * @param listener the listener to send events to
	 */
	public PSelector(PEditorParams params, PSelectorType type, JFrame parent, PSelectorListener listener) {
		this.params = params;
		this.type = type;
		this.parent = parent;
		this.listener = listener;
		init();
		initComponents();
	}

	/**
	 * Returns the selector type of this selector.
	 * @return the selector type
	 * @see    PSelectorType
	 */
	public PSelectorType getType() {
		return type;

	}

	/**
	 * Returns the selected string in the selector.
	 * @return the entered / selected string
	 */
	public String getSelection() {
		return selection;

	}

	/**
	 * Sets the visibility of the selector to true.
	 */
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Creates components for the selector.
	 */
	private void init() {
		frame 			= new JFrame();
		filesContainer 	= new JPanel();
		scrollPane 		= new JScrollPane(filesContainer);
		textField 		= new JTextField(30);
		inputContainer 	= new JPanel();
		submitButton 	= new JButton("Open");
		selection 		= null;
	}

	/**
	 * Initializes all components for the selector.
	 */
	private void initComponents() {
		// init main frame
		frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setLayout(new BorderLayout(0, 0));
		frame.setBackground(params.guiBgColourDark);
		// init scrollable files container
		scrollPane.setPreferredSize(new Dimension(WIDTH, FILE_CONTAINER_HEIGHT));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(params.guiBgColourDark);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setBackground(params.guiInputBgColour);
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = params.guiInputBgColour;
			}
		});
		// adding buttons for each file
		filesContainer.setLayout(null);
		filesContainer.setBorder(null);
		filesContainer.setBackground(params.guiBgColourDark);
		JButton[] buttons = createFileButtons();
		for (JButton b : buttons) filesContainer.add(b);
		filesContainer.setPreferredSize(new Dimension(
					WIDTH, BUTTON_HEIGHT * buttons.length));
		// init container for input bar at bottom of screen
		inputContainer.setLayout(new BorderLayout(0, 0));
		inputContainer.setBackground(params.guiBgColourDark);
		submitButton.setBackground(params.guiBgColour);
		submitButton.setForeground(Color.WHITE);
		submitButton.setBorderPainted(false);
		submitButton.setActionCommand(textField.getText());
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitPressed(textField.getText());
			}
		});
		textField.setBackground(params.guiInputBgColour);
		textField.setForeground(Color.WHITE);
		textField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		inputContainer.add(submitButton, BorderLayout.LINE_END);
		inputContainer.add(textField, BorderLayout.LINE_START);
		// adding components to main frame
		frame.add(scrollPane, BorderLayout.PAGE_START);
		frame.add(inputContainer, BorderLayout.PAGE_END);
		// configuring main frame
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.requestFocusInWindow();
		// automatically close selector when the editor window is closed
		parent.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
	}

	/**
	 * Creates a button for each file in the target directory.
	 * @return array of buttons for each file in the dir
	 */
	private JButton[] createFileButtons() {
		String[] names = findFiles();
		JButton[] buttons = new JButton[names.length];
		for (int i = 0; i < names.length; i++) {
			// JButton button = new JButton(names[i]);
			JButton button = new JButton();
			JLabel label = new JLabel(names[i]);
			label.setBackground(null);
			label.setForeground(Color.WHITE);
			button.setActionCommand(names[i]);
			button.setLayout(new BorderLayout(0, 0));
			button.setBounds(0, i * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
			button.setForeground(Color.WHITE);
			button.setBackground((i % 2 == 0) ? params.guiBgColour : params.guiBgColourDark);
			button.add(label, BorderLayout.WEST);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPressed(e);
				}
			});
			buttons[i] = button;
		}
		return buttons;

	}

	/**
	 * Handles the button pressed event for a file button.
	 * Updates the text field at the bottom with the new file name
	 * @param e the action event that was triggered
	 */
	private void buttonPressed(ActionEvent e) {
		selection = e.getActionCommand();
		textField.setText(e.getActionCommand());
	}

	/**
	 * Handles the submit button pressed event for a file button.
	 * Sends the name of the file to the listener
	 * @param text the string that was in the text field at the bottom of the selector,
	 * 			   this is the name of the selected file
	 */
	private void submitPressed(String text) {
		selection = text;
		listener.selectionMade(this);	
		frame.dispose();
	}

	/**
	 * Finds all files within the editor's resources/map directory.
	 * @return array of names of all the files in the editor's map resource dir
	 */
	private String[] findFiles() {
		PFileReader dirReader = new PFileReader();
		try {
			return dirReader.readDirChildren(PResourceType.PMAP, 
					PModuleName.EDITOR, true);

		} catch (PResourceException e) {
			e.printStackTrace();
			return new String[0];

		}
	}
}
