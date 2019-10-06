package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.RestResponse;

public class IntentResponse extends RestResponse{
    private Intent intent;
    	
	public IntentResponse (Intent intent) {
		super(new Long(200), "OK", "Intent found");
		
		setIntent(intent);
	}  

	private void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	public Intent getIntent() {
		return this.intent;
	}
	
}
