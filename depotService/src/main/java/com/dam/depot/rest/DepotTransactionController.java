package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.DepotTransactionStore;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionResponse;
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
//		RequestBlocker.lockUser(depotRequest.getDepot().getUserId());
		try {
			RestResponse response = new DepotTransactionResponse(depotTransactionStore.getDepotSafe(depotTransactionRequest));
//			RequestBlocker.unlockUser(depotRequest.getDepot().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getDepotTransactionList")
	public RestResponse getDepotTransactionList(@RequestBody DepotTransactionListRequest depotTransactionRequest) throws DamServiceException {
		RequestBlocker.lockUser(depotTransactionRequest.getDepotTransaction().getUserId());
		try {
			RestResponse response = new DepotTransactionListResponse(depotTransactionStore.getDepotListSafe(depotTransactionRequest));
			RequestBlocker.unlockUser(depotTransactionRequest.getDepotTransaction().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}


}