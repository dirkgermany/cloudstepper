package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentCreateResponse extends IntentWriteResponse{

	public IntentCreateResponse(Intent intent) {
		super(new Long(200), "OK", "Intent Saved", intent);
	}
    	
}
