package com.dam.depot.rest.message.accountTransaction;

import java.util.List;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.RestResponse;

public class AccountTransactionListResponse extends RestResponse{
	
	List<AccountTransaction> accountTransactionList;

	public AccountTransactionListResponse(List<AccountTransaction> accountTransactionList) {
		super(200L, "OK", "Accounts Transaction found");
		this.accountTransactionList = accountTransactionList;
	}

}
