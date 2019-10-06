package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.rest.message.RestResponse;

public class AccountTransactionDropResponse extends RestResponse{
    	
	public AccountTransactionDropResponse (Long result) {
		super(result, "OK", "Account Transaction dropped");
	}  
}
