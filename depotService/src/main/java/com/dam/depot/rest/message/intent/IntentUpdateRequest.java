package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentUpdateRequest extends IntentWriteRequest {

    public IntentUpdateRequest(Long requestorUserId, Intent intent) {
    	super (requestorUserId, intent);
    }
}