package com.dam.depot.rest.message.account;

import com.dam.depot.rest.message.RestResponse;

public class AccountDropResponse extends RestResponse{
    	
	public AccountDropResponse (Long result) {
		super(result, "OK", "Account dropped");
	}  
}
