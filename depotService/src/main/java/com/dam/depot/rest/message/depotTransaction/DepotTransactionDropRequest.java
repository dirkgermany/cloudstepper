package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public class DepotTransactionDropRequest extends DepotTransactionWriteRequest {

	public DepotTransactionDropRequest(Long requestorUserId, DepotTransaction depotTransaction) {
		super(requestorUserId, depotTransaction);
    }
}