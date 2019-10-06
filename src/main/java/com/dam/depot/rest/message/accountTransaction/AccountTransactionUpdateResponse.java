package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public class AccountTransactionUpdateResponse extends AccountTransactionWriteResponse{
    	
	public AccountTransactionUpdateResponse (AccountTransaction accountTransaction) {
		super(new Long(200), "OK", "Account Transaction Updated", accountTransaction);
	}  

}
