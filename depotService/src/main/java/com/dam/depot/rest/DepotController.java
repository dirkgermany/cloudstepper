package com.dam.depot.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.store.DepotStore;
import com.dam.exception.DamServiceException;

@RestController
public class DepotController extends MasterController {
	@Autowired
	private DepotStore depotStore;

	@GetMapping("/getDepotPerformance")
	public RestResponse getDepotPerformance(@RequestParam Map<String, String> params) throws DamServiceException {
		Map<String, String> decodedMap = decodeUrlMap(params);
		try {
			return depotStore.getDepotPerformanceSafe(decodedMap);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}


}