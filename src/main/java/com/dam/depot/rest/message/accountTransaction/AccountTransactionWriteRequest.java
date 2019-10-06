package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public abstract class AccountTransactionWriteRequest extends AccountTransactionRequest {

    public AccountTransactionWriteRequest(Long requestorUserId, AccountTransaction accountTransaction) {
    	super(requestorUserId, accountTransaction);
    }
    
}