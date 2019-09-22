package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotCreateRequest extends DepotWriteRequest {

    public DepotCreateRequest(Long requestorUserId, Depot depot) {
		super(requestorUserId, depot);
    }
    
}