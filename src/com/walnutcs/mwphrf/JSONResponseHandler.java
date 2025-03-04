/**
 * 
 */
package com.walnutcs.mwphrf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.HttpEntity;
import org.json.JSONObject;

/**
 * 
 */
public class JSONResponseHandler extends AbstractHttpClientResponseHandler<JSONObject> {

	@Override
	public JSONObject handleEntity(HttpEntity entity) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuilder content = new StringBuilder();
		
		String inStr;
		while ( (inStr = reader.readLine()) != null ) 
			content.append(inStr);

		return new JSONObject(content.toString());
	}



}
