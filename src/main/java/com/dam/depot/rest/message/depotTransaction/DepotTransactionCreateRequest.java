package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public class DepotTransactionCreateRequest extends DepotTransactionWriteRequest {

    public DepotTransactionCreateRequest(Long requestorUserId, DepotTransaction depotTransaction) {
		super(requestorUserId, depotTransaction);
    }
    
}