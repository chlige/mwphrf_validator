/**
 * 
 */
package com.walnutcs.mwphrf.yachtscoring;

import org.json.JSONArray;
import org.json.JSONObject;

import com.walnutcs.mwphrf.BoatEntry;

/**
 * 
 */
public class YSEntry extends BoatEntry {

	private JSONObject objectData;

	public YSEntry(JSONObject objectData) {
		super();
		// TODO Auto-generated constructor stub
		this.objectData = objectData;
		
		JSONObject ownerData = objectData.getJSONObject("owner");
		JSONObject splitData = objectData.getJSONObject("split");
		
		this.yachtName = objectData.isNull("name") ? "" : objectData.getString("name");
		this.sailNumber = objectData.isNull("sailNumber") ? "" : objectData.getString("sailNumber");
		this.makeModel = objectData.isNull("design") ? "" : objectData.getString("design");
		this.ownerName = (ownerData.isNull("firstName") ? "" : ownerData.getString("firstName")+ " " ) + 
				( ownerData.isNull("lastName") ? "" : ownerData.getString("lastName"));
		this.racingCircle = splitData.getString("splitCircle");
		this.racingDivision = splitData.getString("splitDivision");
		this.racingClass = splitData.getString("splitClassName");
		
		if ( objectData.has("ratings") ) {
			JSONArray ratings = objectData.getJSONArray("ratings");
			for ( int r = 0; r < ratings.length() ; r++ ) {
				JSONObject rating = ratings.getJSONObject(r);
				if ( "Rating_PHRF".equalsIgnoreCase(rating.getString("ratingType")) ) {
					this.requestedRating = rating.getInt("value");
					break;
				}
			}
		}
	}
	
	public JSONObject getData() { 
		return this.objectData;
	}
}
