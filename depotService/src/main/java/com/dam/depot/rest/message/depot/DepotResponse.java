package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestResponse;

public class DepotResponse extends RestResponse{
    private Depot construction;
    	
	public DepotResponse (Depot depot) {
		super(new Long(200), "OK", "Depot found");
		
		setDepot(construction);
	}  

	private void setDepot(Depot construction) {
		this.construction = construction;
	}
	
	public Depot getDepot() {
		return this.construction;
	}
	
}
