package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;

public class AssetClassToPortfolioMapUpdateRequest extends AssetClassToPortfolioMapWriteRequest {

    public AssetClassToPortfolioMapUpdateRequest(Long requestorUserId, AssetClassToPortfolioMap map) {
    	super (requestorUserId, map);
    }
}