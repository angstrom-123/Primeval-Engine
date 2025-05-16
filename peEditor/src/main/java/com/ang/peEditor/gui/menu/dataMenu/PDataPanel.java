package com.ang.peEditor.gui.menu.dataMenu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ang.peEditor.PEditorParams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PDataPanel extends JPanel {
	private final PEditorParams params;
	private final int CONTAINER_HEIGHT = 25;
	private final int WIDTH = 200;
	private int height;
	private List<PDataPanelEntry> topPanelEntries = new ArrayList<PDataPanelEntry>();
	private List<PDataPanelEntry> botPanelEntries = new ArrayList<PDataPanelEntry>();
	private PDataChangeListener listener;

	public PDataPanel(PEditorParams params, List<PDataPanelEntry> entries, 
			PDataChangeListener listener) {
		super();
		this.params = params;
		this.listener = listener;
		for (PDataPanelEntry entry : entries) {
			if (entry.panelIndex == PDataPanelEntry.TOP) {
				topPanelEntries.add(entry);
			} else if (entry.panelIndex == PDataPanelEntry.BOT) {
				botPanelEntries.add(entry);
			}
		}
		height = (entries.size() + 2) * CONTAINER_HEIGHT;
		init();
	}

	private void init() {
		JPanel topPanel = initPanel("CORNER", topPanelEntries);
		JPanel botPanel = initPanel("SECTOR", botPanelEntries);
		Dimension d = new Dimension(WIDTH, height);
		setSize(d);
		setPreferredSize(d);
		setLayout(new BorderLayout(0, 0));
		setVisible(true);
		// panel.setLocation(parent.getX() + parent.getWidth() - WIDTH, parent.getY() + 60);
		add(topPanel, BorderLayout.PAGE_START);
		add(botPanel, BorderLayout.PAGE_END);
		revalidate();
		repaint();
	}

	private JPanel initPanel(String label, List<PDataPanelEntry> entries) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.setPreferredSize(new Dimension(WIDTH, (entries.size() + 1) * CONTAINER_HEIGHT));
		JLabel panelLabel = new JLabel(label);
		panelLabel.setForeground(Color.WHITE);
		panelLabel.setVerticalAlignment(JLabel.TOP);
		panelLabel.setHorizontalAlignment(JLabel.CENTER);
		JPanel headingPanel = new JPanel();
		headingPanel.setPreferredSize(new Dimension(WIDTH, CONTAINER_HEIGHT));
		headingPanel.setBackground(params.guiHeadingColour);
		headingPanel.add(panelLabel);
		JPanel dataPanel = addEntriesToPanel(entries);
		panel.add(headingPanel, BorderLayout.PAGE_START);
		panel.add(dataPanel, BorderLayout.PAGE_END);
		return panel;

	}

	private JPanel addEntriesToPanel(List<PDataPanelEntry> entries) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WIDTH, entries.size() * CONTAINER_HEIGHT));
		panel.setBackground(params.guiBgColour);
		panel.setLayout(null);
		for (int i = 0; i < entries.size(); i++) {
			PDataPanelEntry entry = entries.get(i);
			Color labelBackgroundColour = (i % 2 == 0) 
			? params.guiBgColourDark
			: params.guiBgColour;
			JLabel entryLabel = new JLabel(" " + entry.heading + " :");		
			entryLabel.setForeground(Color.WHITE);
			entryLabel.setPreferredSize(new Dimension(WIDTH / 2, CONTAINER_HEIGHT));
			JComponent entryData;
			if (entry.readOnly) {
				entryData = createReadOnlyLabel(entry);
			} else if (entry.booleanOnly) {
				entryData = createBooleanOnlyLabel(entry);
			} else {
				entryData = createEditableLabel(entry);
			}
			entryData.setForeground(Color.WHITE);
			JPanel entryContainer = new JPanel();
			entryContainer.setLayout(new BorderLayout(0, 0));
			entryContainer.setBackground(labelBackgroundColour);
			entryContainer.setBounds(0, i * CONTAINER_HEIGHT, WIDTH, CONTAINER_HEIGHT);
			entryContainer.add(entryLabel, BorderLayout.WEST);
			entryContainer.add(entryData, BorderLayout.CENTER);
			panel.add(entryContainer);
		}
		return panel;

	}

	private JLabel createReadOnlyLabel(PDataPanelEntry entry) {
		JLabel out = new JLabel(" " + entry.data);
		out.setBackground(null);
		return out;

	}

	private JComboBox<String> createBooleanOnlyLabel(PDataPanelEntry entry) {
		JComboBox<String> out = new JComboBox<String>(new String[]{"true", "false"});
		out.setBackground(params.guiInputBgColour);
		out.setSelectedIndex((entry.data.equals("true") ? 0 : 1));
		out.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = (String) out.getSelectedItem();
				listener.dataChange(entry, text);
			}
		});
		return out;

	}

	private JTextField createEditableLabel(PDataPanelEntry entry) { // TODO: make this a spinner
		JTextField out = new JTextField(entry.data, 5);
		out.setBackground(params.guiInputBgColour);
		// out.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		out.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = out.getText();
				listener.dataChange(entry, text);
			}
		});
		out.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				String text = out.getText();
				listener.dataChange(entry, text);
			}

			@Override
			public void focusGained(FocusEvent e) {}
		});
		return out;

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, height);

	}
}
