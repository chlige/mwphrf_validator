package com.walnutcs.mwphrf.phrf;

import java.util.ArrayList;
import java.util.List;

public class PHRFRootNode {

	private List<PHRFMatchList> entries = new ArrayList<PHRFMatchList>();
	
	public int getEntryCount() {
		return this.entries.size();
	}
	
	public PHRFMatchList getEntry(int index) {
		return this.entries.get(index);
	}
	
	public void addEntry(PHRFMatchList entry) {
		this.entries.add(entry);
	}
	
	public void clearEntries() { 
		this.entries.clear();
	}
	
	public List<PHRFMatchList> getEntries() {
		return this.entries;
	}

	@Override
	public String toString() {
		return String.format("PHRF search results - %d boats", this.entries.size());
	}
	
	
}
