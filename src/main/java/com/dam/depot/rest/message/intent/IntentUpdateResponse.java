package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentUpdateResponse extends IntentWriteResponse{
    	
	public IntentUpdateResponse (Intent intent) {
		super(new Long(200), "OK", "Intent Updated", intent);
	}  

}
