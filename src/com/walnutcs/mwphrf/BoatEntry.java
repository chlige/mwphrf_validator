package com.walnutcs.mwphrf;

import java.io.IOException;
import java.util.List;

import com.walnutcs.mwphrf.phrf.PHRFBoatEntry;
import com.walnutcs.mwphrf.phrf.PHRFCertificate;
import com.walnutcs.mwphrf.phrf.PHRFCertificateValues;
import com.walnutcs.mwphrf.phrf.PHRFMatchList;

public class BoatEntry {
	
	protected String yachtName;
	protected String sailNumber;
	protected String makeModel;
	protected String ownerName;
	protected String racingCircle;
	protected String racingDivision;
	protected String racingClass;
	protected Integer requestedRating = null;
	protected String selRatingVariable = "HCP";
	protected PHRFMatchList phrfMatches;
	
	protected BoatEntry() {
		this.phrfMatches = new PHRFMatchList(this);
	}
	
	public BoatEntry(String yachtName, String sailNumber, String makeModel, String ownerName, 
			String racingCircle, String racingDivision, String racingClass) {
		this();
		this.yachtName = yachtName;
		this.sailNumber = sailNumber;
		this.makeModel = makeModel;
		this.ownerName = ownerName;
		this.racingCircle = racingCircle;
		this.racingDivision = racingDivision;
		this.racingClass = racingClass;
	}

	public String getYachtName() {
		return yachtName;
	}

	public String getSailNumber() {
		return sailNumber;
	}

	public String getMakeModel() {
		return makeModel;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getRacingCircle() {
		return racingCircle;
	}

	public String getRacingDivision() {
		return racingDivision;
	}

	public String getRacingClass() {
		return racingClass;
	}
	
	public PHRFCertificate getCertificate() {
		PHRFBoatEntry phrfBoat = this.getMatchList().getSelectedBoat();
		if ( phrfBoat != null )
			return phrfBoat.getCertList().getSelectedCertificate();
		return null;
	}
	
	public void setRatingVariable(String ratingVariable) {
		this.selRatingVariable = ratingVariable;
	}
	
	public String getRatingVariable() {
		return this.selRatingVariable;
	}
	
	public Integer getRating() {
		PHRFCertificate certificate = this.getCertificate();
		
		if ( certificate == null ) { 
			return null;
		}
		
		PHRFCertificateValues values = certificate.getValues();
		
		switch ( this.selRatingVariable ) {
		case "HCP": return values.getHCP();
		case "DHCP": return values.getDHCP();
		case "NSHCP": return values.getNSHCP();
		}
		return null;
	}
	
	public String getRatingString() { 
		Integer rating = this.getRating();
		if ( rating == null )
			return null;
		return String.format("%d (%s)", rating, this.selRatingVariable);

	}
	
	/**
	 * @return the matchedBoats
	 */
	public List<PHRFBoatEntry> getMatchedBoats() {
		return this.phrfMatches.getPHRFBoats();
	}
	
	public PHRFBoatEntry getSelectedMatch() { 
		return this.phrfMatches.getSelectedBoat();
	}
	
	public void setSelectedMatch(PHRFBoatEntry entry) {
		this.phrfMatches.setSelectedBoat(entry);
	}
	
	public PHRFMatchList getMatchList() {
		return this.phrfMatches;
	}
	
	// Default is only to find matches based on sail number and yacht name.  If no boats are found, then try by type.
	public void findMatches() throws IOException { 
		this.phrfMatches.clearBoats();
		this.phrfMatches.runSearch("name", this.getYachtName(), true);

		if ( this.phrfMatches.getPHRFBoats().size() == 0 || this.phrfMatches.getSelectedBoat() == null ) {
			this.phrfMatches.runSearch("sail", this.getSailNumber());	
		}
			
		if ( this.phrfMatches.getPHRFBoats().size() == 0 ) {
			this.phrfMatches.runSearch("type", this.getMakeModel());
		}
	}
	
	public void findMatches(boolean bySailNumber, boolean byYachtName, boolean byMakeModel) throws IOException {
		this.phrfMatches.clearBoats();
		
		if ( bySailNumber ) {
			this.phrfMatches.runSearch("sail", this.getSailNumber());
		}
		
		if ( byYachtName ) {
			this.phrfMatches.runSearch("name", this.getYachtName());
		}
		
		if ( byMakeModel ) {
			this.phrfMatches.runSearch("type", this.getMakeModel());
		}
	}
	
	@Override
	public String toString() {
		return this.sailNumber.concat(" - ").concat(this.yachtName);
	}


}
