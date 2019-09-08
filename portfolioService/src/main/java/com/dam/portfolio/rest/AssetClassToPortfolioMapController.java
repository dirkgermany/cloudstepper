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

@RestController
public class AssetClassToPortfolioMapController {
	@Autowired
	private AssetClassToPortfolioMapStore mapStore;
	
	@PostMapping("addAssetClassesToPortfolio")
	public RestResponse addAssetClassesToPortfolio(@RequestBody AddAssetClassesToPortfolioMapRequest addRequest ) throws DamServiceException {
		try {
			ConstructionMap constructionMap = mapStore.addAssetClassesToPortfolio(addRequest.getPortfolioId(), addRequest.getAssetClassIds());
			return new AddAssetClassesToPortfolioMapResponse(constructionMap);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}