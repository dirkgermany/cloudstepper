package com.dam.portfolio.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.MappedConstructionManager;
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapResponse;
import com.dam.portfolio.rest.message.mappedConstruction.MappedConstructionCreateRequest;
import com.dam.portfolio.rest.message.mappedConstruction.MappedConstructionCreateResponse;
import com.dam.portfolio.rest.message.mappedConstruction.MappedConstructionResponse;

@RestController
public class AssetClassToPortfolioController {
	
	MappedConstructionManager mappedConstructionManager = new MappedConstructionManager();

	@PostMapping("createAssetClassII")
	public RestResponse addAssetClassesToPortfolioII(@RequestBody AddAssetClassesToPortfolioMapRequest addRequest ) throws DamServiceException {
		System.out.println("Scheiße");
		return new AddAssetClassesToPortfolioMapResponse(new ConstructionMap());
	}

	@PostMapping("addAssetClasses")
	public RestResponse addAssetClassesToPortfolioII(@RequestBody String testString ) throws DamServiceException {
		System.out.println("Scheiße");
		return new AddAssetClassesToPortfolioMapResponse(new ConstructionMap());
	}

	
//	@Autowired
//	MappedConstructionManager mappedConstructionManager;
	
	

	/**
	 * @return
	 */
//	@PostMapping("/getConstructionMap")
//	public RestResponse getMappedConstruction(@RequestBody Long portfolioId) throws DamServiceException {
//		try {
//			return new MappedConstructionResponse(mappedConstructionManager.getMappedConstruction(portfolioId));
//		} catch (DamServiceException e) {
//			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
//		}
//	}
//
//	@PostMapping("/createConstructionMap")
//	public RestResponse createMappedConstruction(@RequestBody MappedConstructionCreateRequest createRequest) throws DamServiceException {
//		try {
//			return new MappedConstructionCreateResponse(mappedConstructionManager.storeMappedConstruction(createRequest.getMappedConstruction()));
//		} catch (DamServiceException e) {
//			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
//		}
//	}
//
	@PostMapping("addAssetClassesToPortfolio")
	public RestResponse addAssetClassesToPortfolio(@RequestBody AddAssetClassesToPortfolioMapRequest addRequest ) throws DamServiceException {
		try {
			ConstructionMap constructionMap = mappedConstructionManager.addAssetClassesToPortfolio(addRequest.getPortfolioId(), addRequest.getAssetClassIds());
			return new AddAssetClassesToPortfolioMapResponse(constructionMap);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

//	@PostMapping("/updateAssetClass")
//	public RestResponse updateAssetClass(@RequestBody AssetClassUpdateRequest assetClassUpdateRequest) throws DamServiceException {
//		try {
//			return new AssetClassUpdateResponse(assetClassStore.updateAssetClassSafe(assetClassUpdateRequest));
//		} catch (DamServiceException e) {
//			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
//		}
//	}

}