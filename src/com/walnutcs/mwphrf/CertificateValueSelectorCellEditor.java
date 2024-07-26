/**
 * 
 */
package com.walnutcs.mwphrf;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import com.walnutcs.mwphrf.phrf.PHRFCertificateValues;
import com.walnutcs.mwphrf.phrf.PHRFCertificateValues.PHRFValue;

/**
 * @author George Chlipala
 *
 */
public class CertificateValueSelectorCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CertificateValueSelectorCellEditor() {
		super(new JComboBox<PHRFValue>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		JComboBox<PHRFValue> combo = (JComboBox<PHRFValue>)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        combo.removeAllItems();
        
        if ( value instanceof PHRFCertificateValues ) {
        	PHRFCertificateValues certValues = (PHRFCertificateValues) value;
            for ( PHRFValue aValue : certValues.getValues() ) {
            	combo.addItem(aValue);
            }
            
            if ( certValues.getSelectedValue() != null ) {
            	combo.setSelectedItem(certValues.getSelectedValue());
            }
        }
		return combo;
	}
	
	
}
