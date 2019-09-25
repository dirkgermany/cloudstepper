package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.RestRequest;

public class AccountRequest extends RestRequest {
    private Account account;

    public AccountRequest( Long requestorUserId, Account account) {
		super("DAM 2.0");
		setAccount(account);
		setRequestorUserId(requestorUserId);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
    
}