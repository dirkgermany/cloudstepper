package com.dam.depot.rest.message.account;

import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.filter.RequestFilter;

public class AccountListRequest extends AccountRequest {
	
	private RequestFilter filter;

	public AccountListRequest(Long requestorUserId, Account account, RequestFilter filter) {
		super(requestorUserId, account);
		this.filter = filter;
	}

	public RequestFilter getFilter() {
		return filter;
	}
}
