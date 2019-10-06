package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.RestResponse;

public class AccountTransactionResponse extends RestResponse{
    private AccountTransaction accountTransaction;
    	
	public AccountTransactionResponse (AccountTransaction accountTransaction) {
		super(new Long(200), "OK", "Account Transaction found");
		
		setAccountTransaction(accountTransaction);
	}  

	private void setAccountTransaction(AccountTransaction accountTransaction) {
		this.accountTransaction = accountTransaction;
	}
	
	public AccountTransaction getAccountTransaction() {
		return this.accountTransaction;
	}
	
}
