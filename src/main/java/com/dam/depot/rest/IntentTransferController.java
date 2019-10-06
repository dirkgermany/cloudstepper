package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentTransferToAccountStore;
import com.dam.depot.store.IntentTransferToDepotStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentTransferController {
	
	@Autowired
	private IntentTransferToDepotStore intentTransferToDepotStore;

	@Autowired
	private IntentTransferToAccountStore intentTransferToAccountStore;

	@PostMapping("/transferToDepotIntent")
	public RestResponse intentTransferToDepot(@RequestBody IntentRequest intentTransferRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentTransferRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToDepotStore.transferToDepotIntentSafe(intentTransferRequest));
			RequestBlocker.unlockUser(intentTransferRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentTransferRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/transferToAccountIntent")
	public RestResponse intentTransferToAccount(@RequestBody IntentRequest intentTransferRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentTransferRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToAccountStore.transferToAccountIntentSafe(intentTransferRequest));
			RequestBlocker.unlockUser(intentTransferRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentTransferRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	
	@PostMapping("/transferToDepotConfirmed")
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

	@PostMapping("/transferToDepotDeclined")
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
	
	@PostMapping("/transferToAccountConfirmed")
	public RestResponse intentTransferToAccountConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToAccountStore.confirmTransferToAccountSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/transferToAccountDeclined")
	public RestResponse intentTransferToAccountDeclined(@RequestBody IntentRequest intentDeclinedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentDeclinedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToAccountStore.declineTransferToAccountSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}