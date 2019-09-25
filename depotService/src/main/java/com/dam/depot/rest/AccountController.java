package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.AccountStore;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.account.AccountRequest;
import com.dam.depot.rest.message.account.AccountResponse;
import com.dam.depot.rest.message.account.AccountCreateRequest;
import com.dam.depot.rest.message.account.AccountCreateResponse;
import com.dam.depot.rest.message.account.AccountListRequest;
import com.dam.depot.rest.message.account.AccountListResponse;
import com.dam.exception.DamServiceException;

@RestController
public class AccountController {
	@Autowired
	private AccountStore accountStore;

	/**
	 * Retrieves a person
	 * 
	 * @param personRequest
	 * @return
	 */
	@PostMapping("/getAccount")
	public RestResponse getAccount(@RequestBody AccountRequest accountRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountRequest.getAccount().getUserId());
		try {
			RestResponse response = new AccountResponse(accountStore.getAccountSafe(accountRequest));
			RequestBlocker.unlockUser(accountRequest.getAccount().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getAccountList")
	public RestResponse getAccountList(@RequestBody AccountListRequest accountRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountRequest.getAccount().getUserId());
		try {
			RestResponse response =  new AccountListResponse(accountStore.getAccountListSafe(accountRequest));
			RequestBlocker.unlockUser(accountRequest.getAccount().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createAccount")
	public RestResponse createAccount(@RequestBody AccountCreateRequest accountCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountCreateRequest.getAccount().getUserId());
		try {
			RestResponse response =  new AccountCreateResponse(accountStore.createAccountSafe(accountCreateRequest));
			RequestBlocker.unlockUser(accountCreateRequest.getAccount().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}