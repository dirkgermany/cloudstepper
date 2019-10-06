package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.RestRequest;

public class AccountTransactionRequest extends RestRequest {
    private AccountTransaction accountTransaction;

    public AccountTransactionRequest( Long requestorUserId, AccountTransaction accountTransaction) {
		super("DAM 2.0");
		setAccountTransaction(accountTransaction);
		setRequestorUserId(requestorUserId);
	}

	public AccountTransaction getAccountTransaction() {
		return accountTransaction;
	}

	public void setAccountTransaction(AccountTransaction accountTransaction) {
		this.accountTransaction = accountTransaction;
	}
    
}