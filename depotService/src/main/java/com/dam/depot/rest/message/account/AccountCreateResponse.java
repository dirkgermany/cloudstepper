package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;

public class AccountCreateResponse extends AccountWriteResponse{

	public AccountCreateResponse(Account account) {
		super(new Long(200), "OK", "Account Saved", account);
	}
    	
}
