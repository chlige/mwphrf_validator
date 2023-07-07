/**
 * 
 */
package com.walnutcs.mwphrf;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * @author George Chlipala
 *
 */
public class BoatSelectorCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BoatSelectorCellEditor() {
		super(new JComboBox<PHRFBoatEntry>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		JComboBox<PHRFBoatEntry> combo = (JComboBox<PHRFBoatEntry>)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        combo.removeAllItems();
        
        if ( value instanceof PHRFMatchList ) {
        	PHRFMatchList matchList = (PHRFMatchList) value;
            for ( PHRFBoatEntry boat : matchList.getPHRFBoats() ) {
            	combo.addItem(boat);
            }
            
            if ( matchList.getSelectedBoat() != null ) {
            	combo.setSelectedItem(matchList.getSelectedBoat());
            }
        }
		return combo;
	}
	
	
}
