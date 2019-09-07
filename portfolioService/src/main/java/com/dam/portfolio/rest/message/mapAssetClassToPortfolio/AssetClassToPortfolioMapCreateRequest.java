package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;

public class AssetClassToPortfolioMapCreateRequest extends AssetClassToPortfolioMapWriteRequest {

    public AssetClassToPortfolioMapCreateRequest(Long requestorUserId, AssetClassToPortfolioMap map) {
		super(requestorUserId, map);
    }
    
}