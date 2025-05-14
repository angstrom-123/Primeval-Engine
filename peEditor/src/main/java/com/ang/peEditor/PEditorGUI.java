package com.ang.peEditor;

import java.util.List;
import java.awt.event.*;
import javax.swing.*;

import com.ang.peEditor.dataPanel.PDataChangeListener;
import com.ang.peEditor.dataPanel.PDataPanel;
import com.ang.peEditor.dataPanel.PDataPanelEntry;
import com.ang.peEditor.selector.*;

public class PEditorGUI implements ActionListener, ItemListener, PSelectorListener,
		PDataChangeListener {
	private PGUIRenderer renderer;
	private JFrame frame;
	private PEditorInterface ei;
	private String savedFileName = null;

	public PEditorGUI(PGUIRenderer renderer, PEditorInterface ei) {
		this.renderer = renderer;
		this.frame = renderer.getFrame();	
		this.ei = ei;
	}

	public void init() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createNewMenu());
		frame.setResizable(false);
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.validate();
		frame.repaint();
	}

	public void closeDataPanel() {
		renderer.removeSubPanel();
	}

	public void openDataPanel(List<PDataPanelEntry> entries) {
		renderer.addSubPanel(new PDataPanel(entries, this));
	}

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
			PSelector selector = new PSelector(PSelectorType.OPEN, 
					frame, this);
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
			PSelector selector = new PSelector(PSelectorType.SAVE, 
					frame, this);
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
			int selection = JOptionPane.showConfirmDialog(frame, optionPanel, "New Sector", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				int cornerCount = Integer.valueOf(countField.getText());		
				double scale = Double.valueOf(scaleField.getText());		
				ei.newSector(cornerCount, scale);
			}
			}
			break;

		default:
			break;

		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {}

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

	@Override
	public void dataChange(PDataPanelEntry entry, String text) {
		ei.dataPanelChange(entry, text);
	}

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

	private JMenu createNewMenu() {
		JMenu newMenu = new JMenu("New");
		JMenuItem itemSector = new JMenuItem("Sector");
		itemSector.addActionListener(this);
		newMenu.add(itemSector);
		return newMenu;
	}
}
