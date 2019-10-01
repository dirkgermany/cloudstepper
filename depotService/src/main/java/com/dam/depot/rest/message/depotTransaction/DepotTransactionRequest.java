package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.RestRequest;

public class DepotTransactionRequest extends RestRequest {
    private DepotTransaction depotTransaction;

    public DepotTransactionRequest( Long requestorUserId, DepotTransaction depotTransaction) {
		super("DAM 2.0");
		setDepotTransaction(depotTransaction);
		setRequestorUserId(requestorUserId);
	}

	public DepotTransaction getDepotTransaction() {
		return depotTransaction;
	}

	public void setDepotTransaction(DepotTransaction depotTransaction) {
		this.depotTransaction = depotTransaction;
	}
    
}