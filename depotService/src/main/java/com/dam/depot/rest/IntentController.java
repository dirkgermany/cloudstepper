package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.AccountStore;
import com.dam.depot.IntentStore;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.account.AccountRequest;
import com.dam.depot.rest.message.account.AccountResponse;
import com.dam.depot.rest.message.depot.DepotCreateRequest;
import com.dam.depot.rest.message.depot.DepotCreateResponse;
import com.dam.depot.rest.message.intent.IntentCreateRequest;
import com.dam.depot.rest.message.intent.IntentCreateResponse;
import com.dam.depot.rest.message.intent.IntentListRequest;
import com.dam.depot.rest.message.intent.IntentListResponse;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.rest.message.account.AccountCreateRequest;
import com.dam.depot.rest.message.account.AccountCreateResponse;
import com.dam.depot.rest.message.account.AccountListRequest;
import com.dam.depot.rest.message.account.AccountListResponse;
import com.dam.exception.DamServiceException;

@RestController
public class IntentController {
	@Autowired
	private IntentStore intentStore;


	@PostMapping("/getListIntentInvest")
	public RestResponse getListIntentInvest(@RequestBody IntentRequest intentListRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentListRequest.getRequestorUserId());
		try {
			RestResponse response =  new IntentListResponse(intentStore.getIntentListSafe(intentListRequest));
			RequestBlocker.unlockUser(intentListRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/investIntent")
	public RestResponse investIntent(@RequestBody IntentRequest intentCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(intentCreateRequest.getRequestorUserId());
		try {
			RestResponse response = new IntentCreateResponse(intentStore.investIntentSafe(intentCreateRequest));
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
			RestResponse response = new IntentCreateResponse(intentStore.confirmInvestSafe(intentConfirmedRequest));
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
			RestResponse response = new IntentCreateResponse(intentStore.declineInvestSafe(intentDeclinedRequest));
			RequestBlocker.unlockUser(intentDeclinedRequest.getRequestorUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}