package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public abstract class DepotWriteRequest extends DepotRequest {

	public DepotWriteRequest(Long requestorUserId, Depot depot) {
		super(requestorUserId, depot);
	}

}