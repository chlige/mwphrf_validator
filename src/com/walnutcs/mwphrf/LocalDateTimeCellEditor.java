/**
 * 
 */
package com.walnutcs.mwphrf;

import java.awt.Component;
import java.time.format.DateTimeFormatter;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * 
 */
public class LocalDateTimeCellEditor extends DefaultCellEditor implements TableCellEditor {

	
	private static final long serialVersionUID = 1L;
	private JFormattedTextField field;
	
	public LocalDateTimeCellEditor(DateTimeFormatter format) {
		super(new JFormattedTextField(new LocalDateTimeFormatter(format)));
		this.field = (JFormattedTextField)this.getComponent();
	}

	public LocalDateTimeCellEditor() {
		super(new JFormattedTextField(new LocalDateTimeFormatter()));
		this.field = (JFormattedTextField)this.getComponent();
	}

	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.field.setValue(value);
		return field;
	}
}
