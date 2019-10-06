package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.filter.RequestFilter;

public class DepotTransactionListRequest extends DepotTransactionRequest {
	
	private RequestFilter filter;

	public DepotTransactionListRequest(Long requestorUserId, DepotTransaction depotTransaction, RequestFilter filter) {
		super(requestorUserId, depotTransaction);
		this.filter = filter;
	}

	public RequestFilter getFilter() {
		return filter;
	}
}
