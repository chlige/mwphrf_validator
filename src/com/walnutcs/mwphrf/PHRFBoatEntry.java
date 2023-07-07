package com.walnutcs.mwphrf;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class PHRFBoatEntry {

	private String yachtName = null;
	private String sailNumber = null;
	private String makeModel = null;
	
	private PHRFCertificateList certificates = new PHRFCertificateList();
	
	/**
	 * @param yachtName Name of the boat
	 * @param sailNumber Sail number
	 * @param makeModel Make and model of the boat
	 */
	public PHRFBoatEntry(String yachtName, String sailNumber, String makeModel) {
		this.yachtName = yachtName;
		this.sailNumber = sailNumber;
		this.makeModel = makeModel;
	}
	
	public String getYachtName() { 
		return this.yachtName;
	}
	
	public String getSailNumber() { 
		return this.sailNumber;
	}
	
	public String getMakeModel() { 
		return this.makeModel;
	}
	
	public Collection<PHRFCertificate> getCerts() {
		return this.certificates.getCerts();
	}
	
	public PHRFCertificateList getCertList() { 
		return this.certificates;
	}
	
	public int getCertificateCount() {
		return this.certificates.getCertificateCount();
	}
	
	public Integer getSelectedYear() { 
		return this.certificates.getSelectedYear();
	}
	
	public void setSelectedYear(Integer year) {
		this.certificates.setSelectedYear(year);
	}
	
	public PHRFCertificate getCertificate(int index) {
		return this.certificates.getCertificate(index);
	}
	
	public void addCertificate(PHRFCertificate certificate) {
		this.certificates.addCertificate(certificate);
	}
	
	public boolean hasCertificate(int year) {
		return this.certificates.hasCertificate(year);
	}
	
	public boolean hasCertificate(PHRFCertificate certificate) {
		return this.certificates.hasCertificate(certificate);
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof PHRFBoatEntry ) {
			PHRFBoatEntry anEntry = (PHRFBoatEntry) obj;
			// right now, just compare if it has the same name and sail number.
			return anEntry.yachtName.equalsIgnoreCase(this.yachtName) && anEntry.sailNumber.equalsIgnoreCase(this.sailNumber);
		} else if ( obj instanceof BoatEntry ) {
			BoatEntry anEntry = (BoatEntry) obj;
			return anEntry.getSailNumber().equalsIgnoreCase(this.sailNumber) && anEntry.getYachtName().equalsIgnoreCase(this.yachtName);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return this.sailNumber.concat(" - ").concat(this.yachtName);
	}
}
