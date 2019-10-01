package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.AccountTransactionStore;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionCreateRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionCreateResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionListRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionListResponse;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionResponse;
import com.dam.exception.DamServiceException;

@RestController
public class AccountTransactionController {
	@Autowired
	private AccountTransactionStore accountTransactionStore;

	/**
	 * Retrieves a person
	 * 
	 * @param personRequest
	 * @return
	 */
	@PostMapping("/getAccount")
	public RestResponse getAccount(@RequestBody AccountTransactionRequest accountTransactionRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountTransactionRequest.getAccountTransaction().getUserId());
		try {
			RestResponse response = new AccountTransactionResponse(accountTransactionStore.getAccountSafe(accountTransactionRequest));
			RequestBlocker.unlockUser(accountTransactionRequest.getAccountTransaction().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getAccountList")
	public RestResponse getAccountList(@RequestBody AccountTransactionListRequest accountRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountRequest.getAccountTransaction().getUserId());
		try {
			RestResponse response =  new AccountTransactionListResponse(accountTransactionStore.getAccountListSafe(accountRequest));
			RequestBlocker.unlockUser(accountRequest.getAccountTransaction().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createAccount")
	public RestResponse createAccount(@RequestBody AccountTransactionCreateRequest accountTransactionCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(accountTransactionCreateRequest.getAccountTransaction().getUserId());
		try {
			RestResponse response =  new AccountTransactionCreateResponse(accountTransactionStore.createAccountSafe(accountTransactionCreateRequest));
			RequestBlocker.unlockUser(accountTransactionCreateRequest.getAccountTransaction().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}