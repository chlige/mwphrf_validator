/**
 * 
 */
package com.walnutcs.mwphrf;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * 
 */
public class LocalDateTimeFormatter extends AbstractFormatter {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[mm/dd/yyyy ]HH:mm:ss");
	
	/**
	 * 
	 */
	public LocalDateTimeFormatter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}
	
	public LocalDateTimeFormatter() { }

	@Override
	public Object stringToValue(String text) throws ParseException {
		return this.formatter.parseBest(text, LocalDateTime::from, LocalTime::from);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if ( value instanceof TemporalAccessor)
			return this.formatter.format((TemporalAccessor)value);
		else
			throw new ParseException("Object must be an instance of TemporalAccessor", 0);
	}

}
