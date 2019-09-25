package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;

public class AccountDropRequest extends AccountWriteRequest {

	public AccountDropRequest(Long requestorUserId, Account account) {
		super(requestorUserId, account);
    }
}