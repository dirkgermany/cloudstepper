package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentDepositStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentDepositController {
	
	@Autowired
	private IntentDepositStore intentDepositStore;

	@PostMapping("/depositIntent")
	public RestResponse depositIntent(@RequestBody IntentRequest intentCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentCreateRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDepositStore.depositIntentSafe(intentCreateRequest));
			RequestBlocker.unlockUser(intentCreateRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentCreateRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/intentDepositConfirmed")
	public RestResponse intentDepositConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDepositStore.confirmDepositSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/intentDepositDeclined")
	public RestResponse intentDepositDeclined(@RequestBody IntentRequest intentDeclinedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentDeclinedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDepositStore.declineDepositSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}