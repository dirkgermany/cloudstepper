package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public class AccountTransactionCreateResponse extends AccountTransactionWriteResponse{

	public AccountTransactionCreateResponse(AccountTransaction accountTransaction) {
		super(new Long(200), "OK", "Account Transaction Saved", accountTransaction);
	}
    	
}
