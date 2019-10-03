package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentTransferToDepotStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentTransferController {
	
	@Autowired
	private IntentTransferToDepotStore intentTransferToDepotStore;
	
	@PostMapping("/intentTransferToDepotConfirmed")
	public RestResponse intentTransferToDepotConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToDepotStore.confirmTransferToDepotSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/intentTransferToDepotDeclined")
	public RestResponse intentTransferToDepotDeclined(@RequestBody IntentRequest intentDeclinedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentDeclinedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToDepotStore.declineTransferToDepotSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}