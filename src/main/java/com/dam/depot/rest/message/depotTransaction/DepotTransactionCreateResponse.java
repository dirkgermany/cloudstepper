package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public class DepotTransactionCreateResponse extends DepotTransactionWriteResponse{

	public DepotTransactionCreateResponse(DepotTransaction depotTransaction) {
		super(new Long(200), "OK", "Depot Saved", depotTransaction);
	}
    	
}
