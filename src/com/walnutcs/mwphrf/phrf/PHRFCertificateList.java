package com.walnutcs.mwphrf.phrf;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class PHRFCertificateList implements Comparable<PHRFCertificateList> {

	private SortedMap<Integer, PHRFCertificate> certificates = new TreeMap<Integer, PHRFCertificate>();
	private Integer selYear = null;

	public PHRFCertificateList() {
		// TODO Auto-generated constructor stub
	}

	public Collection<PHRFCertificate> getCerts() {
		return this.certificates.values();
	}
	
	public int getCertificateCount() {
		return this.certificates.size();
	}
	
	public Integer getSelectedYear() { 
		return this.selYear;
	}
	
	public void setSelectedYear(Integer year) {
		this.selYear = year;
	}
	
	public PHRFCertificate getCertificate(int index) {
		return this.certificates.values().toArray(new PHRFCertificate[this.certificates.size()])[index];
	}
	
	public void addCertificate(PHRFCertificate certificate) {
		if ( this.selYear == null || certificate.getYear() > this.selYear ) 
			this.selYear = certificate.getYear();
		this.certificates.put(certificate.getYear(), certificate);
	}
	
	public boolean hasCertificate(int year) {
		return this.certificates.containsKey(year);
	}
	
	public boolean hasCertificate(PHRFCertificate certificate) {
		return this.hasCertificate(certificate.getYear());
	}
	
	public PHRFCertificate getSelectedCertificate() {
		return this.certificates.get(this.selYear);
	}

	@Override
	public String toString() {
		if ( this.selYear == null ) {
			return String.format("%d years", this.certificates.size());
		} else {
			return String.format("%d", this.selYear);
		}
	}

	@Override
	public int compareTo(PHRFCertificateList o) {
		if ( this.selYear != null ) {
			if ( o.selYear != null )
				return this.selYear.compareTo(o.selYear);
			else
				return 1;
		} else if ( o.selYear != null ) {
			return -1;
		} else {
			return this.certificates.size() - o.certificates.size();
		}
	}

}
