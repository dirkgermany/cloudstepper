package com.dam.depot.rest.message.account;

import java.util.List;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.RestResponse;

public class AccountListResponse extends RestResponse{
	
	List<Account> accountList;

	public AccountListResponse(List<Account> accountList) {
		super(200L, "OK", "Accounts found");
		this.accountList = accountList;
	}

}
