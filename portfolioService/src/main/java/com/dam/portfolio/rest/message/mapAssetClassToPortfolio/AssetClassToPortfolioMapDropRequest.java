package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;

public class AssetClassToPortfolioMapDropRequest extends AssetClassToPortfolioMapWriteRequest {

	public AssetClassToPortfolioMapDropRequest(Long requestorUserId, AssetClassToPortfolioMap map) {
		super(requestorUserId, map);
    }
}