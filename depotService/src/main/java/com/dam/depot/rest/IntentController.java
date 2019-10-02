package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionCreateRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionCreateResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionListRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionListResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionCreateRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionCreateResponse;
import com.dam.depot.rest.message.intent.IntentCreateRequest;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentListRequest;
import com.dam.depot.rest.message.intent.IntentListResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.store.IntentInvestStore;
import com.dam.depot.store.IntentStore;
import com.dam.depot.store.IntentTransferToDepotStore;
import com.dam.exception.DamServiceException;

@RestController
public class IntentController {
	@Autowired
	private IntentInvestStore intentInvestStore;
	
	@Autowired
	private IntentTransferToDepotStore intentTransferToDepotStore;


	@PostMapping("/getListIntent")
	public RestResponse getListIntentInvest(@RequestBody IntentRequest intentListRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentListRequest.getRequestorUserId());
		try {
			RestResponse response =  new IntentListResponse(intentInvestStore.getIntentListSafe(intentListRequest));
			RequestBlocker.unlockUser(intentListRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentListRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/investIntent")
	public RestResponse investIntent(@RequestBody IntentRequest intentCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentCreateRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentInvestStore.investIntentSafe(intentCreateRequest));
			RequestBlocker.unlockUser(intentCreateRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/intentInvestConfirmed")
	public RestResponse intentInvestConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentInvestStore.confirmInvestSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/intentInvestDeclined")
	public RestResponse intentInvestDeclined(@RequestBody IntentRequest intentDeclinedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentDeclinedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentInvestStore.declineInvestSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
	
	@PostMapping("/intentTransferToDepotConfirmed")
	public RestResponse intentTransferToDepotConfirmed(@RequestBody IntentRequest intentConfirmedRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentConfirmedRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentTransferToDepotStore.confirmTransferToDepotSafe(intentConfirmedRequest));
			RequestBlocker.unlockUser(intentConfirmedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
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