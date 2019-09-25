package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;

public class AccountUpdateRequest extends AccountWriteRequest {

    public AccountUpdateRequest(Long requestorUserId, Account account) {
    	super (requestorUserId, account);
    }
}