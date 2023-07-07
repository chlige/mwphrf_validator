/**
 * 
 */
package com.walnutcs.mwphrf;

import java.io.IOException;

import javax.swing.table.AbstractTableModel;


/**
 * @author George Chlipala
 *
 */
public class PHRFTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PHRFRootNode phrfRoot;
	private final static String[] colnames = {"", "Sail Number", "Yacht Name", "Make Model", "HCP", "DHCP", "NSHCP"};
	
	private static PHRFTableModel instance;
	
	public static PHRFTableModel getInstance() {
		if ( instance == null ) {
			instance = new PHRFTableModel();
		}
		return instance;
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
	public Class<? extends Object> getColumnClass(int column) {
		switch (column) {
		case 0: 
			return Boolean.class;
		case 1:
		case 2:
			return String.class;
		case 3:
		case 4:
		case 5:
			return Integer.class;
		default:
			return Object.class;
		}
	}
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

/*
	@Override
	public Object getValueAt(Object node, int column) {
		if ( node instanceof PHRFRootNode ) {
			if ( column == 0 ) 
				return "Search results";
		} else if ( node instanceof PHRFMatch ) {
			PHRFMatch match = (PHRFMatch) node;
			switch (column) {
			case 0: return match.getSailNumber();
			case 1: return match.getYachtName();
			case 2: return match.getMakeModel();
			}
		} else if ( node instanceof PHRFBoatEntry ) {
			PHRFBoatEntry boat = (PHRFBoatEntry)node;
			switch (column) {
			case 0: return boat.getSailNumber();
			case 1: return boat.getYachtName();
			case 2: return boat.getMakeModel();
			}
		} else if ( node instanceof PHRFCertificate ) {
			PHRFCertificate cert = (PHRFCertificate)node;
			switch (column) {
			case 2: return String.valueOf(cert.getYear());
			case 3: return cert.getHCP();
			case 4: return cert.getDHCP();
			case 5: return cert.getNSHCP();
			}
		}
		return null;
	}
	*/


		
	public void addBoat(PHRFMatchList entry) {
		this.phrfRoot.addEntry(entry);
	}
	
	public void addBoat(BoatEntry entry) throws IOException {
		this.phrfRoot.addEntry(new PHRFMatchList(entry));
	}
	
	public void clearBoats() {
		int childCount = this.phrfRoot.getEntryCount();
		int[] childIndices = new int[childCount];
		for ( int i = 0 ; i < childCount; i++ ) {
			childIndices[i] = i;
		}
		this.phrfRoot.clearEntries();
	}


}
