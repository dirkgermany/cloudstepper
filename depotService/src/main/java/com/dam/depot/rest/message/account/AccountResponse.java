package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.RestResponse;

public class AccountResponse extends RestResponse{
    private Account account;
    	
	public AccountResponse (Account account) {
		super(new Long(200), "OK", "Account found");
		
		setAccount(account);
	}  

	private void setAccount(Account account) {
		this.account = account;
	}
	
	public Account getAccount() {
		return this.account;
	}
	
}
