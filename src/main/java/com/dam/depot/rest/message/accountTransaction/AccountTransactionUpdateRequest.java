package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public class AccountTransactionUpdateRequest extends AccountTransactionWriteRequest {

    public AccountTransactionUpdateRequest(Long requestorUserId, AccountTransaction accountTransaction) {
    	super (requestorUserId, accountTransaction);
    }
}