package com.walnutcs.mwphrf.phrf;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class PHRFCertificateValues implements Comparable<PHRFCertificateValues> {

	public enum PHRFVariable {
		BHCP, DHCP, HCP, NSHCP
	}

	public class PHRFValue implements Comparable<PHRFValue> {
		private PHRFVariable variable;
		private Number value;
		
		PHRFValue(PHRFVariable variable, Number value) {
			this.variable = variable;
			this.value = value;
		}
		
		public PHRFVariable getVariable() { 
			return this.variable;
		}
		
		public Number getValue() { 
			return this.value;
		}

		@Override
		public String toString() {
			return String.format("%d (%s)", this.value.intValue(), this.variable.toString());
		}

		@Override
		public int compareTo(PHRFValue o) {
			return o.value.intValue() - this.value.intValue();
		} 
	}
	
	private SortedMap<PHRFVariable, PHRFValue> values = new TreeMap<PHRFVariable, PHRFValue>();	
	private PHRFVariable selVariable = null;

	public PHRFCertificateValues(int BHCP, int HCP, int DHCP, int NSHCP) {
		this.values.put(PHRFVariable.BHCP, new PHRFValue(PHRFVariable.BHCP, BHCP));
		this.values.put(PHRFVariable.HCP, new PHRFValue(PHRFVariable.HCP, HCP));
		this.values.put(PHRFVariable.DHCP, new PHRFValue(PHRFVariable.DHCP, DHCP));
		this.values.put(PHRFVariable.NSHCP, new PHRFValue(PHRFVariable.NSHCP, NSHCP));
	}
	
	public int getBHCP() { 
		return this.values.get(PHRFVariable.BHCP).getValue().intValue();
	}
	
	public int getHCP() { 
		return this.values.get(PHRFVariable.HCP).getValue().intValue();
	}
	
	public int getDHCP() {
		return this.values.get(PHRFVariable.DHCP).getValue().intValue();
	}

	public int getNSHCP() { 
		return this.values.get(PHRFVariable.NSHCP).getValue().intValue();
	}

	public void setSelectedVariable(PHRFVariable variable) {
		this.selVariable = variable;
	}
	
	public PHRFVariable getSelectedVariable() { 
		return this.selVariable;
	}
	
	public PHRFValue getSelectedValue() { 
		if ( this.selVariable != null )
			return this.values.get(this.selVariable);
		else 
			return null;
	}
	
	public Collection<PHRFValue> getValues() { 
		return this.values.values();
	}

	@Override
	public String toString() {
		if ( this.selVariable == null ) {
			return "Select value";
		} else {
			return this.getSelectedValue().toString();
		}
	}

	@Override
	public int compareTo(PHRFCertificateValues o) {
		if ( this.selVariable != null )
			return this.getSelectedValue().compareTo(o.getSelectedValue());
		return -1;
	}

	
}
