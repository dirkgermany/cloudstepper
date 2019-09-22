package com.dam.depot.rest.message.depot;

import com.dam.depot.rest.message.RestResponse;

public class DepotDropResponse extends RestResponse{
    	
	public DepotDropResponse (Long result) {
		super(result, "OK", "Depot dropped");
	}  
}
