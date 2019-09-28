package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentDropRequest extends IntentWriteRequest {

	public IntentDropRequest(Long requestorUserId, Intent intent) {
		super(requestorUserId, intent);
    }
}