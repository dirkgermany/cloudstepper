package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;

public class AccountCreateRequest extends AccountWriteRequest {

    public AccountCreateRequest(Long requestorUserId, Account account) {
		super(requestorUserId, account);
    }
    
}