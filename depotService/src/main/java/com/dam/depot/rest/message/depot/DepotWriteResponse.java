package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestResponse;

public abstract class DepotWriteResponse extends RestResponse{
    private Depot depot;
	
	public DepotWriteResponse(Long result, String shortStatus, String longStatus, Depot depot) {
		super(result, shortStatus, longStatus);
		setDepot(depot);
	}

	private void setDepot(Depot depot) {
		this.depot = depot;
	}
	
	public Depot getDepot() {
		return depot;
	}
}
