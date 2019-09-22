package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotDropRequest extends DepotWriteRequest {

	public DepotDropRequest(Long requestorUserId, Depot depot) {
		super(requestorUserId, depot);
    }
}