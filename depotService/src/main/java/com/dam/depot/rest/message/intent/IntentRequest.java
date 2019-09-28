package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.RestRequest;

public class IntentRequest extends RestRequest {
    private Intent intent;

    public IntentRequest( Long requestorUserId, Intent intent) {
		super("DAM 2.0");
		setIntent(intent);
		setRequestorUserId(requestorUserId);
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}
    
}