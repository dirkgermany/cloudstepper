package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.rest.message.RestRequest;

public class AccountStatusRequest extends RestRequest {
	
	private AccountStatus accountStatus;
	
    public AccountStatusRequest( Long requestorUserId, AccountStatus accountStatus) {
		super("DAM 2.0");
		setAccountStatus(accountStatus);
		setRequestorUserId(requestorUserId);
    }

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}
}
