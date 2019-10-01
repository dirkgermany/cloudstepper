package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;

public class AccountTransactionDropRequest extends AccountTransactionWriteRequest {

	public AccountTransactionDropRequest(Long requestorUserId, AccountTransaction accountTransaction) {
		super(requestorUserId, accountTransaction);
    }
}