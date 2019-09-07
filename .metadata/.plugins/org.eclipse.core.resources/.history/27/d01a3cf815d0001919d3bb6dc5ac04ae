package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;
import com.dam.portfolio.rest.message.RestRequest;

public abstract class AssetClassToPortfolioMapWriteRequest extends RestRequest {

	private AssetClassToPortfolioMap map;

    public AssetClassToPortfolioMapWriteRequest(Long requestorUserId, AssetClassToPortfolioMap map) {
		super("DAM 2.0");
    }

	public AssetClassToPortfolioMap getMapAssetClassToPortfolio() {
		return this.map;
	}

	public void setMapAssetClassToPortfolio(AssetClassToPortfolioMap map) {
		this.map = map;
	}
    
}