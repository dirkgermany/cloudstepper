package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.intent.IntentListResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentController {
	@Autowired
	private IntentStore intentStore;



	@PostMapping("/getListIntent")
	public RestResponse getListIntent(@RequestBody IntentRequest intentListRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentListRequest.getRequestorUserId());
		try {
			RestResponse response =  new IntentListResponse(intentStore.getIntentListSafe(intentListRequest));
			RequestBlocker.unlockUser(intentListRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentListRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}	
}