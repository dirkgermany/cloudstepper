package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionResponse;
import com.dam.depot.store.DepotTransactionStore;
import com.dam.exception.DamServiceException;

@RestController
public class DepotTransactionController {
	@Autowired
	private DepotTransactionStore depotTransactionStore;

	/**
	 * Retrieves a depot transaction.
	 * 
	 * @param personRequest
	 * @return
	 */
	@PostMapping("/getDepotTransaction")
	public RestResponse getDepotTransaction(@RequestBody DepotTransactionRequest depotTransactionRequest) throws DamServiceException {
		try {
			return new DepotTransactionResponse(depotTransactionStore.getDepotTransactionSafe(depotTransactionRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getDepotTransactionList")
	public RestResponse getDepotTransactionList(@RequestBody DepotTransactionListRequest depotTransactionRequest) throws DamServiceException {
		try {
			return new DepotTransactionListResponse(depotTransactionStore.getDepotTransactionListSafe(depotTransactionRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}


}