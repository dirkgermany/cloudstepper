package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestResponse;

public class DepotResponse extends RestResponse{
    private Depot depot;
    	
	public DepotResponse (Depot depot) {
		super(new Long(200), "OK", "Depot found");
		
		setDepot(depot);
	}  

	private void setDepot(Depot depot) {
		this.depot = depot;
	}
	
	public Depot getDepot() {
		return this.depot;
	}
	
}
