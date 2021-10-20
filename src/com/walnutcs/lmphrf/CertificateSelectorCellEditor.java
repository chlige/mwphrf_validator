/**
 * 
 */
package com.walnutcs.lmphrf;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * @author George Chlipala
 *
 */
public class CertificateSelectorCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CertificateSelectorCellEditor() {
		super(new JComboBox<PHRFCertificate>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		JComboBox<PHRFCertificate> combo = (JComboBox<PHRFCertificate>)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        combo.removeAllItems();
        
        if ( value instanceof PHRFCertificateList ) {
        	PHRFCertificateList certList = (PHRFCertificateList) value;
            for ( PHRFCertificate cert : certList.getCerts() ) {
            	combo.addItem(cert);
            }
            
            if ( certList.getSelectedYear() != null ) {
            	combo.setSelectedItem(certList.getSelectedCertificate());
            }
        }
		return combo;
	}
	
	
}
