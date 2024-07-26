package com.walnutcs.mwphrf.phrf;

public class PHRFCertificate implements Comparable<PHRFCertificate> {

	private int year;
	private PHRFCertificateValues values;
	private String certURL = null;
	
	/**
	 * @param year Certificate year
	 * @param BHCP
	 * @param HCP
	 * @param DHCP
	 * @param NSHCP
	 */
	public PHRFCertificate(int year, int BHCP, int HCP, int DHCP, int NSHCP, String url) {
		this.year = year;
		this.values = new PHRFCertificateValues(BHCP, HCP, DHCP, NSHCP);
		this.certURL = url;
	}
	
	public int getYear() { 
		return this.year;
	}
	
	public PHRFCertificateValues getValues() {
		return this.values;
	}
	
	public String getURL() { 
		return this.certURL;
	}
	
	@Override
	public int compareTo(PHRFCertificate o) {
		return this.year - o.year;
	}
	
	@Override
	public String toString() { 
		return String.valueOf(this.year);
	}
	
}
