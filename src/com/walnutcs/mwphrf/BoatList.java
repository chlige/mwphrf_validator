/**
 * 
 */
package com.walnutcs.mwphrf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import com.walnutcs.mwphrf.PHRFCertificateValues.PHRFValue;

/**
 * @author George Chlipala
 *
 */
public class BoatList extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BoatEntry> entries = new ArrayList<BoatEntry>();
	
	private SortedSet<String> racingCircles = new TreeSet<String>();
	private SortedSet<String> racingDivisions = new TreeSet<String>();
	private SortedSet<String> racingClasses = new TreeSet<String>();
	
	private Map<String, Map<String, SortedSet<String>>> classMap = new HashMap<String, Map<String, SortedSet<String>>>();

	final static String[] colnames = {"Sail Number", "Yacht Name", "Make Model", "Owner", 
			"Circle", "Division", "Class", "PHRF Match", "Certificate Year", "Rating"};

	private static BoatList instance;
	
	public static BoatList getInstance() {
		if ( instance == null ) {
			instance = new BoatList();
		}
		return instance;
	}

	/**
	 * 
	 */
	public BoatList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getRowCount() {
		return this.entries.size();
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
	public boolean isCellEditable(int row, int col) {
		return col == 7 || col == 8 || col == 9 ;
//		return false;
	}

	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch ( columnIndex ) {
		case 7: return PHRFMatchList.class;
		case 8: return PHRFCertificateList.class;
		case 9: return PHRFCertificateValues.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		BoatEntry entry = this.entries.get(rowIndex);
		PHRFBoatEntry selBoat;
		
		switch (columnIndex) {
		case 0: return entry.getSailNumber();
		case 1: return entry.getYachtName();
		case 2: return entry.getMakeModel();
		case 3: return entry.getOwnerName();
		case 4: return entry.getRacingCircle();
		case 5: return entry.getRacingDivision();
		case 6: return entry.getRacingClass();
		case 7: return entry.getMatchList();
		case 8: 
			selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				return selBoat.getCertList();
			} else {
				return null;
			}
		case 9:
			selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				PHRFCertificate selCert = selBoat.getCertList().getSelectedCertificate();
				if ( selCert != null ) {
					return selCert.getValues();
				} else {
					return null;
				}
			} else {
				return null;
			}
		default: return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		BoatEntry entry = this.entries.get(rowIndex);
		PHRFBoatEntry selBoat;
		
		switch ( columnIndex ) {
		case 7: 
			entry.setSelectedMatch((PHRFBoatEntry) aValue); 
			break;
		case 8:
			selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				selBoat.setSelectedYear( ((PHRFCertificate) aValue).getYear() );	
			}
			break;
		case 9:
			selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				PHRFCertificate selCert = selBoat.getCertList().getSelectedCertificate();
				if ( selCert != null ) {
					selCert.getValues().setSelectedVariable(((PHRFValue)aValue).getVariable());
				}
			}
		}
	}

	public BoatEntry getBoat(int rowIndex) {
		return this.entries.get(rowIndex);
	}
	
	public void addBoat(BoatEntry entry) {
		this.entries.add(entry);
		
		String racingCircle = entry.getRacingCircle();
		String racingDiv = entry.getRacingDivision();
		String racingClass = entry.getRacingClass();
		
		this.racingCircles.add(racingCircle);
		this.racingDivisions.add(racingDiv);
		this.racingClasses.add(racingClass);
		Map<String, SortedSet<String>> divMap;
		if ( this.classMap.containsKey(racingCircle) ) {
			divMap = this.classMap.get(racingCircle);
		} else {
			divMap = new HashMap<String, SortedSet<String>>();
			this.classMap.put(racingCircle, divMap);
		}
		if ( ! divMap.containsKey(racingDiv) ) {
			divMap.put(racingDiv, new TreeSet<String>());
		}
		divMap.get(racingDiv).add(racingClass);
	}
	
	public SortedSet<String> getCircles() {
		return this.racingCircles;
	}

	public SortedSet<String> getDivisions() {
		return this.racingDivisions;
	}
	
	public SortedSet<String> getDivisions(String circle) {
		if ( this.classMap.containsKey(circle) ) {
			return new TreeSet<String>(this.classMap.get(circle).keySet());			
		} else {
			return new TreeSet<String>();
		}
	}
	 
	public SortedSet<String> getClasses(String circle, String division) {
		if ( this.classMap.containsKey(circle) && this.classMap.get(circle).containsKey(division) ) {
			return this.classMap.get(circle).get(division);
		} else {
			return new TreeSet<String>();
		}
	}

	public SortedSet<String> getClasses() { 
		return this.racingClasses;
	}

	public void clear() { 
		this.classMap.clear();
		this.entries.clear();
		this.racingCircles.clear();
		this.racingClasses.clear();
		this.racingDivisions.clear();
	}

}
