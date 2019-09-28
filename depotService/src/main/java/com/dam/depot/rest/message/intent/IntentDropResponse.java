package com.dam.depot.rest.message.intent;

import com.dam.depot.rest.message.RestResponse;

public class IntentDropResponse extends RestResponse{
    	
	public IntentDropResponse (Long result) {
		super(result, "OK", "Intent dropped");
	}  
}
