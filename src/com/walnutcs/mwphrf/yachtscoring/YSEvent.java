package com.walnutcs.mwphrf.yachtscoring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.walnutcs.mwphrf.JSONResponseHandler;


public class YSEvent {

	static final String BASE_URL = "https://api.yachtscoring.com/v1/public/";
	
	static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private String id;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean isActive;
	
	private JSONObject objectData;
	
	/*
	"sort": "startDate.desc",
    "or_name_contains": query,
    "or_hostClub.name_contains": query,
    "authorizedOnly": "false",
    "page": 1,
    "size": 100,
    "isActive_eq": "true" 
	*/
	
	public static List<YSEvent> search(String query, JProgressBar progress) throws URISyntaxException, IOException {
		// Build the basic URI.
		URIBuilder searchURI = new URIBuilder(BASE_URL + "event");
		searchURI.addParameter("sort", "startDate.desc");
		searchURI.addParameter("authorizedOnly", "false");
		searchURI.addParameter("size", "100");
		searchURI.addParameter("isActive_eq", "true");
		searchURI.addParameter("or_name_contains", query);
		searchURI.addParameter("or_hostClub.name_contains", query);
		
		// Setup the variables for counting the entries. To ensure we get them all.
		int page = 1;
		int count = -1;
		int size = 100;
		
		boolean hasMore = true;
		
		List<YSEvent> events = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		JSONResponseHandler jsonHandler = new JSONResponseHandler();
		
		if ( progress != null ) {
			progress.setIndeterminate(true);
			progress.updateUI();
		}
		
		while ( hasMore ) {
			URI thisURI = searchURI.setParameter("page", Integer.toString(page)).build();
			System.err.println("Search URL: " + thisURI.toString());
			HttpGet req = new HttpGet(thisURI);
			JSONObject resp = httpClient.execute(req, jsonHandler);
			
			if ( count == -1 ) {
				// Set the count as this is the initial request
				count = resp.getInt("count");	
				events = new ArrayList<YSEvent>(count);
				if ( progress != null ) {
					progress.setIndeterminate(false);
					progress.setMaximum(count);
					progress.setValue(0);
					progress.updateUI();
				}
			} 

			JSONArray rows = resp.getJSONArray("rows");
			
			for ( int i = 0; i < rows.length(); i++ ) {
				events.add(new YSEvent(rows.getJSONObject(i)));
				if ( progress != null ) { 
					if ( i % 10 == 0 ) {
						progress.setValue( progress.getValue() + i);
						progress.updateUI();
					}
				}
			}
			
			if ( progress != null ) {
				progress.setValue(((page - 1) * size) + rows.length() );				
				progress.updateUI();
			}
			
			if ( count < ( page * size ) ) 
				hasMore = false;
			else 
				page++;
		}
		
		httpClient.close();
		
		return events;		
	}
	
	public YSEvent(JSONObject data) {
		this.objectData = data;
		this.id = Integer.toString(data.getInt("id"));
		this.name = data.getString("name");
		this.startDate = LocalDateTime.parse(data.getString("startDate"), formatter).toLocalDate();
		this.endDate = LocalDateTime.parse(data.getString("endDate"), formatter).toLocalDate();
		this.isActive = data.getBoolean("isActive");
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public LocalDate getStartDate() { 
		return this.startDate;
	}
	
	public LocalDate getEndDate() { 
		return this.endDate;
	}
	
	public boolean isActive() { 
		return this.isActive;
	}
	
	public JSONObject getData() { 
		return this.objectData;
	}
	
	public List<YSEntry> getEntries() throws URISyntaxException, IOException { 
		// Build the basic URI.
		URIBuilder searchURI = new URIBuilder(BASE_URL + "event/" + this.id + "/scratch-sheet");
		searchURI.addParameter("sort", "split.splitCircle.asc,split.startSequence.asc,split.splitDivision.asc,split.splitClass.asc,name.asc,id.asc");
		searchURI.addParameter("includeSubclass", "false");
		searchURI.addParameter("divisionGrouping", "true");

		CloseableHttpClient httpClient = HttpClients.createDefault();
		JSONResponseHandler jsonHandler = new JSONResponseHandler();
		
		HttpGet req = new HttpGet(searchURI.build());
		JSONObject resp = httpClient.execute(req, jsonHandler);
		
		JSONArray dataArray = resp.getJSONArray("data");
		
		List<YSEntry> entries = new ArrayList<YSEntry>();
		
		for ( int c = 0; c < dataArray.length(); c++ ) {
			JSONObject circleData = dataArray.getJSONObject(c);
			JSONArray divisionList = circleData.getJSONArray("splitDivisions");
			for ( int d = 0; d < divisionList.length(); d++ ) {
				JSONObject divData = divisionList.getJSONObject(d);
				JSONArray classList = divData.getJSONArray("splitClasses");
				for ( int s = 0; s < classList.length(); s++ ) {
					JSONObject classData = classList.getJSONObject(s);
					JSONArray boatList = classData.getJSONArray("eventBoats");
					for ( int b = 0; b < boatList.length(); b++ ) {
						JSONObject boatData = boatList.getJSONObject(b);
						entries.add(new YSEntry(boatData));
					}
				}
			}			
		}
		
		httpClient.close();
		
		return entries;
	}

}
