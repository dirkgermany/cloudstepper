package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public abstract class DepotTransactionWriteRequest extends DepotTransactionRequest {

	public DepotTransactionWriteRequest(Long requestorUserId, DepotTransaction depotTransaction) {
		super(requestorUserId, depotTransaction);
	}

}