package com.dam.depot.rest.message.depot;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestRequest;

public class DepotRequest extends RestRequest {
    private Depot depot;

    public DepotRequest( Long requestorUserId, Depot depot) {
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