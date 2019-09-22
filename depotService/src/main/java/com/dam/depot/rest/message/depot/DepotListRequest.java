package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotListRequest extends DepotRequest {
	
	private RequestFilter filter;

	public DepotListRequest(Long requestorUserId, Depot depot, RequestFilter filter) {
		super(requestorUserId, depot);
		this.filter = filter;
	}

	public RequestFilter getFilter() {
		return filter;
	}
}
