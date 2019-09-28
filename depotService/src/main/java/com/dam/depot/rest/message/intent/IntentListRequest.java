package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.filter.RequestFilter;

public class IntentListRequest extends IntentRequest {
	
	public IntentListRequest(Long requestorUserId, Intent intent) {
		super(requestorUserId, intent);
	}

}
