package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentCreateRequest extends IntentWriteRequest {

    public IntentCreateRequest(Long requestorUserId, Intent intent) {
		super(requestorUserId, intent);
    }
    
}