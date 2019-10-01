package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public class AccountTransactionCreateRequest extends AccountTransactionWriteRequest {

    public AccountTransactionCreateRequest(Long requestorUserId, AccountTransaction accountTransaction) {
		super(requestorUserId, accountTransaction);
    }
    
}