package com.dam.depot.rest.message.depotTransaction;

import java.util.List;

import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.RestResponse;

public class DepotTransactionListResponse extends RestResponse{
	
	List<DepotTransaction> depotList;

	public DepotTransactionListResponse(List<DepotTransaction> depotList) {
		super(200L, "OK", "Depot Transactions found");
		this.depotList = depotList;
	}

}
