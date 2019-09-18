package com.dam.portfolio.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.AssetClassStore;
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestRequest;
import com.dam.portfolio.rest.message.RestResponse;
import com.dam.portfolio.rest.message.assetClass.AssetClassCreateRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassCreateResponse;
import com.dam.portfolio.rest.message.assetClass.AssetClassDropRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassDropResponse;
import com.dam.portfolio.rest.message.assetClass.AssetClassListResponse;
import com.dam.portfolio.rest.message.assetClass.AssetClassRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassResponse;
import com.dam.portfolio.rest.message.assetClass.AssetClassUpdateRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassUpdateResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapResponse;

@RestController
public class AssetClassController {
	@Autowired
	private AssetClassStore assetClassStore;
	
	@PostMapping("/getAssetClassList")
	public RestResponse getAssetClassList(@RequestBody AssetClassRequest assetClassListRequest) throws DamServiceException {
		try {
			return new AssetClassListResponse(assetClassStore.getAssetClassListSafe(assetClassListRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
	
	@PostMapping("/getAssetClass")
	public RestResponse getAssetClass(@RequestBody AssetClassRequest assetClassRequest) throws DamServiceException {
		try {
			return new AssetClassResponse(assetClassStore.getAssetClassSafe(assetClassRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createAssetClass")
	public RestResponse createAssetClass(@RequestBody AssetClassCreateRequest assetClassCreateRequest) throws DamServiceException {
		try {
			return new AssetClassCreateResponse(assetClassStore.createAssetClassSafe(assetClassCreateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropAssetClass")
	public RestResponse dropAssetClass(@RequestBody AssetClassDropRequest assetClassDropRequest) throws DamServiceException {
		try {
			return new AssetClassDropResponse(assetClassStore.dropAssetClassSafe(assetClassDropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updateAssetClass")
	public RestResponse updateAssetClass(@RequestBody AssetClassUpdateRequest assetClassUpdateRequest) throws DamServiceException {
		try {
			return new AssetClassUpdateResponse(assetClassStore.updateAssetClassSafe(assetClassUpdateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}