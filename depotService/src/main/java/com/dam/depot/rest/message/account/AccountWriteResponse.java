package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.RestResponse;

public abstract class AccountWriteResponse extends RestResponse{
    private Account account;
	
	public AccountWriteResponse(Long result, String shortStatus, String longStatus, Account account) {
		super(result, shortStatus, longStatus);
		setAccount(account);
	}

	private void setAccount(Account account) {
		this.account = account;
	}
	
	public Account getAccount() {
		return account;
	}
}
