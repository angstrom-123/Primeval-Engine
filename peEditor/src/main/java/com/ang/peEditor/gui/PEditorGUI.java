package com.ang.peEditor.gui;

import java.util.List;
import java.awt.event.*;
import javax.swing.*;

import com.ang.peEditor.PEditorInterface;
import com.ang.peEditor.PEditorParams;
import com.ang.peEditor.gui.menu.dataMenu.*;
import com.ang.peEditor.gui.menu.rmbMenu.*;
import com.ang.peEditor.gui.menu.selectorMenu.*;

/**
 * Handles rendering displaying and receiving input on the GUI.
 */
public class PEditorGUI implements ActionListener, ItemListener, PSelectorListener,
		PDataChangeListener, PRMBPanelListener {
	private final PEditorParams params;
	private PGUIRenderer renderer;
	private JFrame frame;
	private PEditorInterface ei;
	private String savedFileName = null;

	/**
	 * Constructs a new gui handler.
	 * @param params   the parameters to use for the gui 
	 * @param renderer the renderer to display the gui with 
	 * @param ei 	   the interface to send events to
	 */
	public PEditorGUI(PEditorParams params, PGUIRenderer renderer, PEditorInterface ei) {
		this.params = params;
		this.renderer = renderer;
		this.frame = renderer.getFrame();	
		this.ei = ei;
	}

	/**
	 * Initializes the gui by creating the menu bar.
	 */
	public void init() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createNewMenu());
		menuBar.add(createConfigMenu());
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.validate();
		frame.repaint();
	}

	/**
	 * Closes all sub panels that have type PDataPanel.
	 * @see com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 */
	public void closeDataPanels() {
		renderer.clearSubPanelsOfType(PDataPanel.class);
	}

	/**
	 * Closes all sub panels that have type PRMBPanel.
	 * @see com.ang.peEditor.gui.menu.rmbMenu.PRMBPanel
	 */
	public void closeRMBPanels() {
		renderer.clearSubPanelsOfType(PRMBPanel.class);
	}

	/**
	 * Opens a new data panel containing the given entries.
	 * @param entries list of data panel entries to display
	 * @see 		  com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 * @see 		  com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry
	 */
	public void openDataPanel(List<PDataPanelEntry> entries) {
		renderer.addSubPanel(new PDataPanel(params, entries, this), PGUIRenderer.LOCATION_RIGHT);
	}

	/**
	 * Opens a new right click menu at the given coordinates, for the given corner.
	 * @param x 		  screen space x coordinate to opent the menu at
	 * @param y 		  screen space y coordinate to opent the menu at
	 * @param sectorIndex index of the sector containing the corner that the menu is for
	 * @param cornerIndex index of the corner that the menu is for
	 * @see 		      com.ang.peEditor.gui.menu.rmbMenu.PRMBPanel
	 */
	public void openRightClickMenu(int x, int y, int sectorIndex, int cornerIndex) {
		renderer.addSubPanel(new PRMBPanel(params, sectorIndex, cornerIndex, this), x, y);
	}

	/**
	 * Handles the action performed event for the menu bar buttons.
	 * @param e the action event that was triggered
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "New": 
			{
			if (savedFileName != null) {
				int selection = JOptionPane.showConfirmDialog(frame, 
						"Save before exiting?", "New", JOptionPane.YES_NO_OPTION);
				if (selection == JOptionPane.YES_OPTION) {
					ei.save(savedFileName);
					ei.newFile();
				} else if (selection == JOptionPane.NO_OPTION) {
					ei.newFile();
				}
			} else {
				ei.newFile();
			}
			}
			break;
			
		case "Open": 
			{
			PSelector selector = new PSelector(params, PSelectorType.OPEN, frame, this);
			selector.show();
			}
			break;

		case "Save":
			if (savedFileName != null) {
				ei.save(savedFileName);
			} else {
				System.out.println("nothing to save");
			}
			break;

		case "Save As": 
			{
			PSelector selector = new PSelector(params, PSelectorType.SAVE, frame, this);
			selector.show();
			}
			break;

		case "Exit": 
			{
			if (savedFileName != null) {
				int selection = JOptionPane.showConfirmDialog(frame, 
						"Save before exiting?", "Exit", JOptionPane.YES_NO_OPTION);
				if (selection == JOptionPane.YES_OPTION) {
					ei.save(savedFileName);
					ei.exit();
				} else if (selection == JOptionPane.NO_OPTION) {
					ei.exit();
				}
			} else {
				ei.exit();
			}
			}
			break;

		case "Undo":
			ei.undo();
			break;

		case "Redo":
			ei.redo();
			break;

		case "Sector":
			{
			JTextField countField = new JTextField(5);
			JTextField scaleField = new JTextField(5);
			JPanel optionPanel = new JPanel();
			optionPanel.add(new JLabel("Corner count:"));
			optionPanel.add(countField);
			optionPanel.add(new JLabel("New sector scale:"));
			optionPanel.add(scaleField);
			int selection = JOptionPane.showConfirmDialog(frame, optionPanel, "New Sector", 
					JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				int cornerCount = Integer.valueOf(countField.getText());		
				double scale = Double.valueOf(scaleField.getText());		
				ei.newSector(cornerCount, scale);
			}
			}
			break;

		case "Spawn Position":
			{
			JTextField xField = new JTextField(5);
			JTextField yField = new JTextField(5);
			JPanel optionPanel = new JPanel();
			optionPanel.add(new JLabel("x:"));
			optionPanel.add(xField);
			optionPanel.add(new JLabel("y:"));
			optionPanel.add(yField);
			int selection = JOptionPane.showConfirmDialog(frame, optionPanel, "Spawn Coordinates", 
					JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				double x = Double.valueOf(xField.getText());		
				double y = Double.valueOf(yField.getText());		
				ei.changePosition(x, y);
			}
			}
			break;

		case "Spawn Facing":
			{
			JTextField xField = new JTextField(5);
			JTextField yField = new JTextField(5);
			JPanel optionPanel = new JPanel();
			optionPanel.add(new JLabel("x:"));
			optionPanel.add(xField);
			optionPanel.add(new JLabel("y:"));
			optionPanel.add(yField);
			int selection = JOptionPane.showConfirmDialog(frame, optionPanel, "Spawn Facing Vector", 
					JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				double x = Double.valueOf(xField.getText());		
				double y = Double.valueOf(yField.getText());		
				ei.changeFacing(x, y);
			}
			}
			break;

		default:
			break;

		}
	}

	/**
	 * Overrides the item state change event.
	 * This currently does nothing.
	 * @param e the item event that was triggered
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {}

	/**
	 * Handles the selection made event for the PSelector.
	 * @param selector the selector that triggered the event
	 * @see 		   com.ang.peEditor.gui.menu.selectorMenu.PSelector
	 */
	@Override
	public void selectionMade(PSelector selector) {
		PSelectorType type = selector.getType();
		String selection = selector.getSelection();
		switch (type) {
		case OPEN:
			ei.open(selection);
			savedFileName = selection;
			break;

		case SAVE:
			ei.save(selection);
			savedFileName = selection;
			break;

		}
	}

	/**
	 * Handles the data change event triggered by a PDataPanel.
	 * @param entry the entry that changed
	 * @param text 	the new value of the entru
	 * @see 		com.ang.peEditor.gui.menu.dataMenu.PDataPanel
	 * @see 		com.ang.peEditor.gui.menu.dataMenu.PDataPanelEntry
	 */
	@Override
	public void dataChange(PDataPanelEntry entry, String text) {
		ei.dataPanelChange(entry, text);
	}

	/**
	 * Handles the action performed event for the right click menu.
	 * @param cornerIndex the index of the corner that was changed
	 * @param sectorIndex the index of the sector containing the corner that changed
	 * @param action 	  the action that was performed on the corner
	 */
	@Override
	public void rmbActionPerformed(int cornerIndex, int sectorIndex, String action) {
		if (action.equals(PRMBPanelActionType.DELETE_CORNER.getAction())) {
			ei.delCorner(cornerIndex, sectorIndex);
		} else if (action.equals(PRMBPanelActionType.DELETE_SECTOR.getAction())) {
			ei.delSector(sectorIndex);
		} else if (action.equals(PRMBPanelActionType.ADD_CORNER_LEFT.getAction())) {
			ei.insCornerLeft(cornerIndex, sectorIndex);
		} else if (action.equals(PRMBPanelActionType.ADD_CORNER_RIGHT.getAction())) {
			ei.insCornerRight(cornerIndex, sectorIndex);
		}
	}

	/**
	 * Handles the right click menu mouse exit event by closing the menu.
	 */
	@Override
	public void rmbMouseExit() {
		renderer.clearSubPanelsOfType(PRMBPanel.class);
	}

	/**
	 * Returns a new JMenu to use for the upper menu bar.
	 * @return a new JMenu to use for the upper menu bar
	 */
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem itemNew = new JMenuItem("New");
		JMenuItem itemOpen = new JMenuItem("Open");
		JMenuItem itemSave = new JMenuItem("Save");
		JMenuItem itemSaveAs = new JMenuItem("Save As");
		JMenuItem itemExit = new JMenuItem("Exit");
		itemNew.addActionListener(this);
		itemOpen.addActionListener(this);
		itemSave.addActionListener(this);
		itemSaveAs.addActionListener(this);
		itemExit.addActionListener(this);
		fileMenu.add(itemNew);
		fileMenu.add(itemOpen);
		fileMenu.add(itemSave);
		fileMenu.add(itemSaveAs);
		fileMenu.add(itemExit);
		return fileMenu;

	}

	/**
	 * Creates a new JMenu for the create options.
	 * @return a new JMenu for the create submenu
	 */
	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		JMenu submenuPreferences = new JMenu("Preferences");
		JMenuItem itemUndo = new JMenuItem("Undo");
		JMenuItem itemRedo = new JMenuItem("Redo");
		// TODO: preferences submenu
		itemUndo.addActionListener(this);
		itemRedo.addActionListener(this);
		editMenu.add(itemUndo);
		editMenu.add(itemRedo);
		editMenu.add(submenuPreferences);
		return editMenu;

	}

	/**
	 * Creates a new JMenu for the new options.
	 * @return a new JMenu for the new submenu
	 */
	private JMenu createNewMenu() {
		JMenu newMenu = new JMenu("New");
		JMenuItem itemSector = new JMenuItem("Sector");
		itemSector.addActionListener(this);
		newMenu.add(itemSector);
		return newMenu;

	}

	/**
	 * Creates a new JMenu for the configuration menu.
	 * @return a new JMenu for the configuration submenu
	 */
	private JMenu createConfigMenu() {
		JMenu newMenu = new JMenu("Config");
		JMenuItem itemSpawnPos = new JMenuItem("Spawn Position");
		JMenuItem itemSpawnFacing = new JMenuItem("Spawn Facing");
		itemSpawnPos.addActionListener(this);
		itemSpawnFacing.addActionListener(this);
		newMenu.add(itemSpawnPos);
		newMenu.add(itemSpawnFacing);
		return newMenu;

	}
}
