package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.DepotStore;
import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.depot.DepotCreateRequest;
import com.dam.depot.rest.message.depot.DepotCreateResponse;
import com.dam.depot.rest.message.depot.DepotListRequest;
import com.dam.depot.rest.message.depot.DepotListResponse;
import com.dam.depot.rest.message.depot.DepotRequest;
import com.dam.depot.rest.message.depot.DepotResponse;
import com.dam.depot.rest.message.depot.DepotUpdateRequest;
import com.dam.depot.rest.message.depot.DepotUpdateResponse;
import com.dam.exception.DamServiceException;

@RestController
public class DepotController {
	@Autowired
	private DepotStore depotStore;

	/**
	 * Retrieves a person
	 * 
	 * @param personRequest
	 * @return
	 */
	@PostMapping("/getDepot")
	public RestResponse getDepot(@RequestBody DepotRequest depotRequest) throws DamServiceException {
		RequestBlocker.lockUser(depotRequest.getDepot().getUserId());
		try {
			RestResponse response = new DepotResponse(depotStore.getDepotSafe(depotRequest));
			RequestBlocker.unlockUser(depotRequest.getDepot().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getDepotList")
	public RestResponse getDepotList(@RequestBody DepotListRequest depotRequest) throws DamServiceException {
		RequestBlocker.lockUser(depotRequest.getDepot().getUserId());
		try {
			RestResponse response = new DepotListResponse(depotStore.getDepotListSafe(depotRequest));
			RequestBlocker.unlockUser(depotRequest.getDepot().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	/**
	 * Transfer From Depot To User Account (minus value) - transfer between both
	 * money accounts - send information to Depot Bank
	 * 
	 * @param depotCreateRequest
	 * @return
	 * @throws DamServiceException
	 */
	@PostMapping("/transferToAccount")
	public RestResponse transferToAccount(@RequestBody DepotRequest depotRequest) throws DamServiceException {
		RequestBlocker.lockUser(depotRequest.getDepot().getUserId());
		try {
			RestResponse response = new DepotResponse(depotStore.transferToAccountSafe(depotRequest));
			RequestBlocker.unlockUser(depotRequest.getDepot().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	/**
	 * User wants to increase the investment.
	 * @param depotCreateRequest
	 * @return
	 * @throws DamServiceException
	 */
	@PostMapping("/deposit")
	public RestResponse createDepot(@RequestBody DepotCreateRequest depotCreateRequest) throws DamServiceException {
		RequestBlocker.lockUser(depotCreateRequest.getDepot().getUserId());
		try {
			RestResponse response = new DepotCreateResponse(depotStore.depositSafe(depotCreateRequest));
			RequestBlocker.unlockUser(depotCreateRequest.getDepot().getUserId());
			return response;
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}