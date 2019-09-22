package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;

public class DepotUpdateResponse extends DepotWriteResponse{
    	
	public DepotUpdateResponse (Depot depot) {
		super(new Long(200), "OK", "Depot Updated", depot);
	}  

}
