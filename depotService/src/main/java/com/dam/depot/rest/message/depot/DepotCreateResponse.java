package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotCreateResponse extends DepotWriteResponse{

	public DepotCreateResponse(Depot depot) {
		super(new Long(200), "OK", "Depot Saved", depot);
	}
    	
}
