package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;

public class AccountUpdateResponse extends AccountWriteResponse{
    	
	public AccountUpdateResponse (Account account) {
		super(new Long(200), "OK", "Account Updated", account);
	}  

}
