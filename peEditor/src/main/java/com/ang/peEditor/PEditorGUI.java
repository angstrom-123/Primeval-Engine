package com.ang.peEditor;

import java.awt.event.*;
import javax.swing.*;

import com.ang.peLib.resources.*;
import com.ang.peEditor.selector.*;

public class PEditorGUI implements ActionListener, ItemListener, PSelectorListener {
	private JFrame frame;
	private PEditorInterface ei;

	public PEditorGUI(PGUIRenderer renderer, PEditorInterface ei) {
		this.frame = renderer.getFrame();	
		this.ei = ei;
	}

	public void init() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		frame.setResizable(false);
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.validate();
		frame.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "New": {
			int selection = JOptionPane.showConfirmDialog(frame, 
					"Save before exiting?", "New", JOptionPane.YES_NO_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				ei.save(null);
				ei.newFile();
			} else if (selection == JOptionPane.NO_OPTION) {
				ei.newFile();
			}
			break;
			
		}
		case "Open": {
			PSelector selector = new PSelector(PSelectorType.OPEN, 
					frame, PResourceManager.MAP_DIR, this);
			selector.show();
			break;

		}
		case "Save":
			ei.save(null);
			break;

		case "Save As": {
			PSelector selector = new PSelector(PSelectorType.SAVE, 
					frame, PResourceManager.MAP_DIR, this);
			selector.show();
			break;

		}
		case "Exit": {
			int selection = JOptionPane.showConfirmDialog(frame, 
					"Save before exiting?", "Exit", JOptionPane.YES_NO_OPTION);
			if (selection == JOptionPane.YES_OPTION) {
				ei.save(null);
				ei.exit();
			} else if (selection == JOptionPane.NO_OPTION) {
				ei.exit();
			}
			break;

		}
		case "Undo":
			ei.undo();
			break;

		case "Redo":
			ei.redo();
			break;

		case "Sector":
			ei.newSector();
			break;

		case "Corner":
			ei.newCorner();
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
		switch (type) {
		case OPEN:
			ei.open(selector.getSelection());
			break;

		case SAVE:
			ei.save(selector.getSelection());
			break;

		}
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
		JMenu submenuNew = new JMenu("New");
		JMenu submenuPreferences = new JMenu("Preferences");
		JMenuItem itemUndo = new JMenuItem("Undo");
		JMenuItem itemRedo = new JMenuItem("Redo");
		JMenuItem itemSector = new JMenuItem("Sector");
		JMenuItem itemCorner = new JMenuItem("Corner");
		// TODO: preferences submenu
		itemUndo.addActionListener(this);
		itemRedo.addActionListener(this);
		itemSector.addActionListener(this);
		itemCorner.addActionListener(this);
		editMenu.add(itemUndo);
		editMenu.add(itemRedo);
		editMenu.add(submenuNew);
		editMenu.add(submenuPreferences);
		submenuNew.add(itemSector);
		submenuNew.add(itemCorner);
		return editMenu;

	}
}
