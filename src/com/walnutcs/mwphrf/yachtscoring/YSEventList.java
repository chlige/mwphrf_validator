/**
 * 
 */
package com.walnutcs.mwphrf.yachtscoring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

/**
 * 
 */
public class YSEventList extends AbstractTableModel {
	
	private static final long serialVersionUID = -8859073103257409546L;

	private final List<YSEvent> events = new ArrayList<YSEvent>();

	final static String[] colnames = {"Event", "Start Date", "End Date", "Active"};
	
	
	private static YSEventList instance;
	
	public static YSEventList getInstance() {
		if ( instance == null ) {
			instance = new YSEventList();
		}
		return instance;
	}
	
	/**
	 * 
	 */
	public YSEventList() {

	}
	
	public void runSearch(String query, JProgressBar progress) throws URISyntaxException, IOException {
		this.events.clear();
		this.events.addAll(YSEvent.search(query, progress));
	}
	
	public void runSearch(String query) throws URISyntaxException, IOException {
		this.events.clear();
		this.events.addAll(YSEvent.search(query, null));
	}

	@Override
	public int getRowCount() {
		return events.size();
	}

	@Override
	public int getColumnCount() {
		return colnames.length;
	}

	@Override
	public String getColumnName(int column) {
		return colnames[column];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch ( columnIndex ) {
		case 1: 
		case 2: 
			return LocalDate.class;
		case 3:
			return Boolean.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		YSEvent event = this.events.get(rowIndex);
		
		switch(columnIndex) {
		case 0: return event.getName();
		case 1: return event.getStartDate();
		case 2: return event.getEndDate();
		case 3: return event.isActive();
		default: return null;
		}
	}
	
	public YSEvent getEvent(int index) {
		return this.events.get(index);
	}
	
	public void clear() { 
		this.events.clear();
	}

}
