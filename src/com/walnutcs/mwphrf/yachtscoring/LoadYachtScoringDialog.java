/**
 * 
 */
package com.walnutcs.mwphrf.yachtscoring;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

/**
 * 
 */
public class LoadYachtScoringDialog extends JDialog {
	
	private YSEvent selectedEvent = null;
	private JFrame frame;
	private JTable eventTable;
	private JTextField searchField;
	private JButton searchButton;
	private JButton selectButton;
	private JProgressBar progressBar;
		
	public static YSEvent showSelectionDialog(JFrame parent, String title) {
		LoadYachtScoringDialog dialog = new LoadYachtScoringDialog(parent, title);
		dialog.setVisible(true);
		return dialog.selectedEvent;
	}
	
	/**
	 * 
	 */
	public LoadYachtScoringDialog(JFrame parent, String title) {
		super(parent, title, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(parent); 
		
		this.initialize();		
	}

	public void initialize() { 
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		mainPanel.add(topPanel, BorderLayout.NORTH);

		topPanel.add(new JLabel("Query:"));

		this.searchField = new JTextField();
		this.searchField.setPreferredSize(new Dimension(250, 25));
		topPanel.add(this.searchField);

		this.searchButton = new JButton("Search");
		searchButton.addActionListener(new RunSearchAction());
		topPanel.add(searchButton);
		topPanel.add(Box.createHorizontalGlue());
		
		// Setup entry table
		this.eventTable = new JTable(YSEventList.getInstance());
		this.eventTable.setShowGrid(true);
		this.eventTable.setFillsViewportHeight(true);
		this.eventTable.setRowSelectionAllowed(true);
		this.eventTable.setColumnSelectionAllowed(false);
		this.eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ListSelectionModel selectionModel = this.eventTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() { 
			public void valueChanged(ListSelectionEvent e) {
				if ( e.getValueIsAdjusting() ) 
					return;
				selectButton.setEnabled(eventTable.getSelectedRowCount() == 1);
			}
		});
				
		this.eventTable.setRowSorter(new TableRowSorter<YSEventList>(YSEventList.getInstance()));
		
		// Create scroll pane for table
		JScrollPane scrollPane = new JScrollPane(this.eventTable);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.setMinimumSize(new Dimension(10,25));
		
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		this.progressBar = new JProgressBar(0, 10);
		this.progressBar.setPreferredSize(new Dimension(50, 10));
		this.progressBar.setValue(5);
		bottomPanel.add(this.progressBar);
		
		bottomPanel.add(Box.createHorizontalGlue());
		
		this.selectButton = new JButton("Load Selected Event");
		this.selectButton.setEnabled(false);
		this.selectButton.addActionListener(new SelectEventAction());
		bottomPanel.add(this.selectButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				selectedEvent = null;
				setVisible(false);
				searchField.setText("");
				YSEventList.getInstance().clear();
				eventTable.updateUI();
			}
		});
		bottomPanel.add(cancelButton);
				
		this.add(mainPanel);
	}
	
	public YSEvent getSelectedEvent() {
		return this.selectedEvent;
	}
	
	private class RunSearchAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			String queryString = searchField.getText();
			if ( queryString.length() == 0 ) {
				JOptionPane.showMessageDialog(null, "Must provide a query.", "Error", JOptionPane.ERROR_MESSAGE);
				searchField.requestFocus();
				return;
			}
			
			searchButton.setEnabled(false);
			searchField.setEnabled(false);
			progressBar.setIndeterminate(true);
			progressBar.setValue(10);
			progressBar.updateUI();
			
			YSEventList eventList = YSEventList.getInstance();
			try {
				eventList.runSearch(queryString, progressBar);
				eventTable.updateUI();
			} catch (URISyntaxException | IOException e1) {
				JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			progressBar.setValue(0);
			progressBar.setIndeterminate(false);
			searchField.setEnabled(true);
			searchButton.setEnabled(true);
			progressBar.updateUI();
		}
	}
	
	private class SelectEventAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			int selRow = eventTable.getSelectedRow();
			int selIndex = eventTable.convertRowIndexToModel(selRow);
			
			selectedEvent = YSEventList.getInstance().getEvent(selIndex);
			setVisible(false);
			searchField.setText("");
			YSEventList.getInstance().clear();
			eventTable.updateUI();
		}
		
	}
}
