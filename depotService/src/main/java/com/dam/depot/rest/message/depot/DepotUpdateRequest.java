package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotUpdateRequest extends DepotWriteRequest {

    public DepotUpdateRequest(Long requestorUserId, Depot depot) {
    	super (requestorUserId, depot);
    }
}