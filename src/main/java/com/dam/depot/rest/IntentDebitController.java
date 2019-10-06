package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentDebitStore;
import com.dam.depot.store.IntentDepositStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentDebitController {
	
	@Autowired
	private IntentDebitStore intentDebitStore;

	@PostMapping("/debitIntent")
	public RestResponse debitIntent(@RequestBody IntentRequest intentCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentCreateRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDebitStore.debitIntentSafe(intentCreateRequest));
			RequestBlocker.unlockUser(intentCreateRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentCreateRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/debitConfirmed")
	public RestResponse intentDebitConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDebitStore.confirmDebitSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/debitDeclined")
	public RestResponse intentDebitDeclined(@RequestBody IntentRequest intentDeclinedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentDeclinedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentDebitStore.declineDebitSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}