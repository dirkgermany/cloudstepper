package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.rest.message.RestResponse;

public class AccountStatusResponse extends RestResponse {

	private AccountStatus accountStatus;

	public AccountStatusResponse(AccountStatus accountStatus) {
		super(new Long(200), "OK", "Request performed");
		setAccountStatus(accountStatus);
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}
}
