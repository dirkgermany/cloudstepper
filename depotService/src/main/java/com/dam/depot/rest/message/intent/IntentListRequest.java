package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;

public class IntentListRequest extends IntentRequest {
	
	public IntentListRequest(Long requestorUserId, Intent intent) {
		super(requestorUserId, intent);
	}

}
