package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.RestResponse;

public abstract class DepotTransactionWriteResponse extends RestResponse{
    private DepotTransaction depotTransaction;
	
	public DepotTransactionWriteResponse(Long result, String shortStatus, String longStatus, DepotTransaction depotTransaction) {
		super(result, shortStatus, longStatus);
		setDepotTransaction(depotTransaction);
	}

	private void setDepotTransaction(DepotTransaction depotTransaction) {
		this.depotTransaction = depotTransaction;
	}
	
	public DepotTransaction getDepotTransaction() {
		return depotTransaction;
	}
}
