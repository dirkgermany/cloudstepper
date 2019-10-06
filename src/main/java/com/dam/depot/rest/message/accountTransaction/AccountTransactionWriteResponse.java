package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.RestResponse;

public abstract class AccountTransactionWriteResponse extends RestResponse{
    private AccountTransaction accountTransaction;
	
	public AccountTransactionWriteResponse(Long result, String shortStatus, String longStatus, AccountTransaction accountTransaction) {
		super(result, shortStatus, longStatus);
		setAccountTransaction(accountTransaction);
	}

	private void setAccountTransaction(AccountTransaction accountTransaction) {
		this.accountTransaction = accountTransaction;
	}
	
	public AccountTransaction getAccountTransaction() {
		return accountTransaction;
	}
}
