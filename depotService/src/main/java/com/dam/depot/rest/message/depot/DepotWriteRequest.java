package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestRequest;

public abstract class DepotWriteRequest extends RestRequest {

	private Depot depot = new Depot();	

    public DepotWriteRequest(Long requestorUserId, Depot depot) {
		super("DAM 2.0");
		setDepot(depot);
		setRequestorUserId(requestorUserId);
    }
        
    public Depot getDepot() {
    	return depot;
    }

	public void setDepot(Depot depot) {
		this.depot = depot;
	}
    
}