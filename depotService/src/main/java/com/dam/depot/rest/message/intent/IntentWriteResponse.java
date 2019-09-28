package com.dam.depot.rest.message.intent;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.RestResponse;

public abstract class IntentWriteResponse extends RestResponse{
    private Intent intent;
	
	public IntentWriteResponse(Long result, String shortStatus, String longStatus, Intent intent) {
		super(result, shortStatus, longStatus);
		setIntent(intent);
	}

	private void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	public Intent getIntent() {
		return intent;
	}
}
