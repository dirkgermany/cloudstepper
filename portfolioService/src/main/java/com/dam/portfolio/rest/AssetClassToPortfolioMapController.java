package com.dam.portfolio.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.AssetClassToPortfolioMapStore;
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.GetAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.GetAssetClassesToPortfolioMapResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.RemoveAssetClassesFromPortfolioMapRequest;

@RestController
public class AssetClassToPortfolioMapController {
	@Autowired
	private AssetClassToPortfolioMapStore mapStore;
	
	@PostMapping("/addAssetClassesToPortfolio")
	public RestResponse addAssetClassesToPortfolio(@RequestBody AddAssetClassesToPortfolioMapRequest addRequest) throws DamServiceException {
		try {
			return mapStore.addAssetClassesToPortfolioSafe(addRequest);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@PostMapping("/getPortfolioConstruction")
	public RestResponse getPortfolioConstructionSafe(@RequestBody GetAssetClassesToPortfolioMapRequest getRequest)  throws DamServiceException {
		try {
			return mapStore.getMapPortfolioSafe(getRequest);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
	
	@PostMapping("/removeAssetClassesFromPortfolio")
	public RestResponse removeAssetClassesFromPortfolio(@RequestBody RemoveAssetClassesFromPortfolioMapRequest removeRequest) throws DamServiceException {
		try {
			return mapStore.removeAssetClassesFromPortfolioSafe(removeRequest);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
	


}