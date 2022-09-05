package com.walnutcs.lmphrf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PHRFMatchList {

	private BoatEntry entry;
	private List<PHRFBoatEntry> phrfBoats = new ArrayList<PHRFBoatEntry>();
	private PHRFBoatEntry selectedBoat;
	
//	private static String SEARCH_URL = "https://www.lmphrf.org/index.php/display-a-handicap";
	private static String SEARCH_URL = "https://mwphrf.org/index.php/display-a-handicap";
	
	public PHRFMatchList(BoatEntry entry) {
		this.entry = entry;
	}

	/**
	 * @return the entry
	 */
	public BoatEntry getEntry() {
		return entry;
	}

	public String getYachtName() { 
		return this.entry.getYachtName();
	}
	
	public String getSailNumber() { 
		return this.entry.getSailNumber();
	}
	
	public String getMakeModel() { 
		return this.entry.getMakeModel();
	}
	

	/**
	 * @return the phrfBoats
	 */
	public List<PHRFBoatEntry> getPHRFBoats() {
		return phrfBoats;
	}
	
	
	public void addPHRFBoat(PHRFBoatEntry boat) {
		this.phrfBoats.add(boat);
	}

	public PHRFBoatEntry getSelectedBoat() {
		return this.selectedBoat;
	}
	
	public void setSelectedBoat(PHRFBoatEntry boat) {
		this.selectedBoat = boat;
	}
	
	void clearBoats() { 
		this.phrfBoats.clear();
	}
	
	void runSearch(String queryType, String queryValue) throws IOException {
		this.runSearch(queryType, queryValue, false);
	}
	
	void runSearch(String queryType, String queryValue, boolean exactMatch) throws IOException {		
		// Setup the query data
		
		/*
		 * 		StringBuffer postData = new StringBuffer();
		postData.append("searchtype=");
		postData.append(queryType);
		postData.append("submit=Submit&searchvalue=");
		postData.append(URLEncoder.encode(queryValue, "UTF-8"));

		URL url = new URL(SEARCH_URL);

		 */
			
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("searchtype", queryType);
		postData.put("searchvalue", queryValue);
		postData.put("submit", "Submit");
		
		// Perform the HTTP POST
		Connection conn = Jsoup.connect(SEARCH_URL).data(postData);
		Document doc = conn.post();
		
		// Read the response
		int responseCode = conn.response().statusCode();
		if ( responseCode != 200 ) {
			throw new IOException(String.format("Invalid HTTP response: %d %s", responseCode, conn.response().statusMessage()));
		}
	
		Elements allTables = doc.select("table");
		
		ListIterator<Element> tableIter = allTables.listIterator();
		
		TABLE_LOOP: while ( tableIter.hasNext() ) {
			Element aTable = tableIter.next();
			Elements tableRows = aTable.select("tr");
			
			// Get the header row of the table.
			Element headerTR = tableRows.first();
			Elements headerTH = headerTR.select("th");
			ListIterator<Element> headerIter = headerTH.listIterator();

			// Parse the header to get the order of the columns
			List<String> headers = new ArrayList<String>();
			while ( headerIter.hasNext() ) {
				headers.add(headerIter.next().text());
			}
			
			// If there is not a BHCP column in the table, then skip.
			if ( ! headers.contains("BHCP") ) {
				continue TABLE_LOOP;
			}

			// Get indices of the various columns.
			int sailNumberCol = headers.indexOf("Sail #");
			int yachtNameCol = headers.indexOf("Yacht Name");
			int makeModelCol = headers.indexOf("Make-Model");
			int issuedCol = headers.indexOf("Issued on");
			int validCol = headers.indexOf("Valid For");
			int BHCPcol = headers.indexOf("BHCP");
			int DHCPcol = headers.indexOf("DHCP");
			int HCPcol = headers.indexOf("HCP");
			int NSHCPcol = headers.indexOf("NSHCP");
			
			int urlCol = headers.size() - 1;

			// Now iterator over the following rows and add the data.
			ListIterator<Element> rowIter = tableRows.listIterator(1);
			
			ROW_LOOP: while ( rowIter.hasNext() ) {
				Element thisRow = rowIter.next();
				Elements rowData = thisRow.select("td");
				
				if ( rowData.size() <= yachtNameCol || rowData.size() <= sailNumberCol || rowData.size() <= makeModelCol ) {
					continue ROW_LOOP;
				}
				
				String yachtName = rowData.get(yachtNameCol).text();
				String sailNumber = rowData.get(sailNumberCol).text();
				String makeModel = rowData.get(makeModelCol).text();
				
								
				PHRFBoatEntry anEntry = new PHRFBoatEntry(yachtName, sailNumber, makeModel);
				
				// Check to see if this boat has already been added to the list.
				int entryIndex = this.phrfBoats.indexOf(anEntry);
				if ( entryIndex ==  -1 ) {
					this.phrfBoats.add(anEntry);
				} else {
					anEntry = this.phrfBoats.get(entryIndex);
				}
				
				// Check if there is already a certificate entry for this year.  
				// If so, go to the next row
				int certYear = Integer.parseInt(rowData.get(validCol).text());
				if ( anEntry.hasCertificate(certYear) ) {
					continue ROW_LOOP;
				}
				
				// Get the values for the certificate
				// For right now, just strip off any notes/restrictions for the numbers
				int valueBHCP = Integer.parseInt(rowData.get(BHCPcol).text().replaceAll("[^-0-9]+", ""));
				int valueHCP = Integer.parseInt(rowData.get(HCPcol).text().replaceAll("[^-0-9]+", ""));
				int valueDHCP = Integer.parseInt(rowData.get(DHCPcol).text().replaceAll("[^-0-9]+", ""));
				int valueNSHCP = Integer.parseInt(rowData.get(NSHCPcol).text().replaceAll("[^-0-9]+", ""));
				
				String urlCell = rowData.get(urlCol).select("a").attr("href");
				
				// Add the certificate to the boat
				PHRFCertificate aCert = new PHRFCertificate(certYear, valueBHCP, valueHCP, valueDHCP, valueNSHCP, urlCell);
				anEntry.addCertificate(aCert);
			} // end of ROW_LOOP
		} // end of TABLE_LOOP

		if ( this.phrfBoats.size() == 1 ) {
			this.selectedBoat = this.phrfBoats.get(0);
		} else {
			List<PHRFBoatEntry> possBoats = new ArrayList<PHRFBoatEntry>();
			ENTRY_LOOP: for ( PHRFBoatEntry anEntry : this.phrfBoats ) {
				if ( anEntry.getSailNumber().equalsIgnoreCase(this.entry.getSailNumber()) &&
						anEntry.getYachtName().equalsIgnoreCase(this.entry.getYachtName()) ) {
					this.selectedBoat = anEntry;
					possBoats.clear();
					break ENTRY_LOOP;
				} else if ( ! exactMatch ) {	
					if ( anEntry.getSailNumber().equalsIgnoreCase(this.entry.getSailNumber()) ) {
						possBoats.add(anEntry);
					} else if ( anEntry.getYachtName().equalsIgnoreCase(this.entry.getYachtName()) ) {
						possBoats.add(anEntry);
					}
				}
			}
			if ( possBoats.size() == 1 ) {
				this.selectedBoat = possBoats.get(0);
			}
		}
	}
	
	@Override
	public String toString() {
		if ( this.selectedBoat != null ) {
			return this.selectedBoat.toString();
		} else {
			return String.format("%d matches", this.phrfBoats.size());
		}
	}
}
