package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;

public class DepotTransactionUpdateResponse extends DepotTransactionWriteResponse{
    	
	public DepotTransactionUpdateResponse (DepotTransaction depotTransaction) {
		super(new Long(200), "OK", "Depot Transaction Updated", depotTransaction);
	}  

}
