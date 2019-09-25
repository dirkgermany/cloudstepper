package com.dam.depot.rest.message.depot;

import java.util.List;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestResponse;

public class DepotListResponse extends RestResponse{
	
	List<Depot> depotList;

	public DepotListResponse(List<Depot> depotList) {
		super(200L, "OK", "Depots found");
		this.depotList = depotList;
	}

}
