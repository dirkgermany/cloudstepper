package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.RestResponse;

public class DepotTransactionResponse extends RestResponse{
    private DepotTransaction depotTransaction;
    	
	public DepotTransactionResponse (DepotTransaction depotTransaction) {
		super(new Long(200), "OK", "Depot Transaction found");
		
		setDepotTransaction(depotTransaction);
	}  

	private void setDepotTransaction(DepotTransaction depotTransaction) {
		this.depotTransaction = depotTransaction;
	}
	
	public DepotTransaction getDepotTransaction() {
		return this.depotTransaction;
	}
	
}
