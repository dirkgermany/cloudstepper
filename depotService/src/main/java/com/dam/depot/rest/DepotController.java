package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.DepotStore;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.depot.DepotCreateRequest;
import com.dam.depot.rest.message.depot.DepotCreateResponse;
import com.dam.depot.rest.message.depot.DepotDropRequest;
import com.dam.depot.rest.message.depot.DepotDropResponse;
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
		try {
			return new DepotResponse(depotStore.getDepotSafe(depotRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createDepot")
	public RestResponse createDepot(@RequestBody DepotCreateRequest depotCreateRequest) throws DamServiceException {
		try {
			return new DepotCreateResponse(depotStore.createDepotSafe(depotCreateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropDepot")
	public RestResponse dropDepot(@RequestBody DepotDropRequest depotDropRequest) throws DamServiceException {
		try {
			return new DepotDropResponse(depotStore.dropDepotSafe(depotDropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updateDepot")
	public RestResponse updateDepot(@RequestBody DepotUpdateRequest depotUpdateRequest) throws DamServiceException {
		try {
			return new DepotUpdateResponse(depotStore.updateDepotSafe(depotUpdateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}