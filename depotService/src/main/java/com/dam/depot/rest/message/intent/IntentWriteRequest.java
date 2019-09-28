package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public abstract class IntentWriteRequest extends IntentRequest {

    public IntentWriteRequest(Long requestorUserId, Intent intent) {
    	super(requestorUserId, intent);
    }
    
}