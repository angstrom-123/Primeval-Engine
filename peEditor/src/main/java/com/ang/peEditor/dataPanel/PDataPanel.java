package com.ang.peEditor.dataPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class PDataPanel extends JPanel {
	private final Color headingColour = new Color(0x353542);
	private final Color backgroundColour = new Color(0x454555);
	private final Color backgroundColourDark = new Color(0x404050);
	private final Color inputBackgroundColour = new Color(0x4e4e5e);
	private final int CONTAINER_HEIGHT = 25;
	private final int WIDTH = 200;
	private int height;
	private List<PDataPanelEntry> topPanelEntries = new ArrayList<PDataPanelEntry>();
	private List<PDataPanelEntry> botPanelEntries = new ArrayList<PDataPanelEntry>();
	private PDataChangeListener listener;

	public PDataPanel(List<PDataPanelEntry> entries, PDataChangeListener listener) {
		super();
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
		setBackground(Color.RED);
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
		headingPanel.setBackground(headingColour);
		headingPanel.add(panelLabel);
		JPanel dataPanel = addEntriesToPanel(entries);
		panel.add(headingPanel, BorderLayout.PAGE_START);
		panel.add(dataPanel, BorderLayout.PAGE_END);
		return panel;

	}

	private JPanel addEntriesToPanel(List<PDataPanelEntry> entries) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WIDTH, entries.size() * CONTAINER_HEIGHT));
		panel.setBackground(backgroundColour);
		panel.setLayout(null);
		for (int i = 0; i < entries.size(); i++) {
			PDataPanelEntry entry = entries.get(i);
			Color labelBackgroundColour = (i % 2 == 0) 
			? backgroundColourDark
			: backgroundColour;
			// JLabel entryLabel = new JLabel(" " + entry.heading + ": " + entry.data);		
			JLabel entryLabel = new JLabel(" " + entry.heading + " :");		
			entryLabel.setForeground(Color.WHITE);
			entryLabel.setPreferredSize(new Dimension(WIDTH / 2, CONTAINER_HEIGHT));
			JComponent entryData;
			if (entry.readOnly) {
				entryData = new JLabel(" " + entry.data);
				entryData.setBackground(labelBackgroundColour);
			} else {
				entryData = new JTextField(entry.data, 5);
				entryData.setBackground(inputBackgroundColour);
				entryData.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, height);

	}
}
