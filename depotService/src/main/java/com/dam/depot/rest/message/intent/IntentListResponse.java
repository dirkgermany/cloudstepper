package com.dam.depot.rest.message.intent;

import java.util.List;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.RestResponse;

public class IntentListResponse extends RestResponse{
	
	List<Intent> intentList;

	public IntentListResponse(List<Intent> intentList) {
		super(200L, "OK", "Intents found");
		this.intentList = intentList;
	}

}
