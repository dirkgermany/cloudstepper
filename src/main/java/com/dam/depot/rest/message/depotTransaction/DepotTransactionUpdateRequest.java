package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public class DepotTransactionUpdateRequest extends DepotTransactionWriteRequest {

    public DepotTransactionUpdateRequest(Long requestorUserId, DepotTransaction depotTransaction) {
    	super (requestorUserId, depotTransaction);
    }
}