package com.dam.depot.rest.message.depotTransaction;

import com.dam.depot.rest.message.RestResponse;

public class DepotTransactionDropResponse extends RestResponse{
    	
	public DepotTransactionDropResponse (Long result) {
		super(result, "OK", "Depot Transaction dropped");
	}  
}
