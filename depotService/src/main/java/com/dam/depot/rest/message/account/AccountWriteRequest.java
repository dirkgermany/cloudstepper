package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.RestRequest;

public abstract class AccountWriteRequest extends AccountRequest {

    public AccountWriteRequest(Long requestorUserId, Account account) {
    	super(requestorUserId, account);
    }
    
}