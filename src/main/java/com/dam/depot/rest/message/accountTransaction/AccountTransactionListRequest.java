package com.dam.depot.rest.message.accountTransaction;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.filter.RequestFilter;

public class AccountTransactionListRequest extends AccountTransactionRequest {
	
	private RequestFilter filter;

	public AccountTransactionListRequest(Long requestorUserId, AccountTransaction accountTransaction, RequestFilter filter) {
		super(requestorUserId, accountTransaction);
		this.filter = filter;
	}

	public RequestFilter getFilter() {
		return filter;
	}
}
