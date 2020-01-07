package com.dam.job.rest.message;

import com.dam.job.messageClass.Intent;

public class RestRequestIntent extends RestRequest {
	
	private Intent intent;

	public RestRequestIntent(Intent intent) {
		super("CS 0.0.1");
		this.setIntent(intent);
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

}
