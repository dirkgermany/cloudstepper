package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;
import com.dam.portfolio.rest.message.RestResponse;

public abstract class AssetClassToPortfolioMapWriteResponse extends RestResponse{
    private AssetClassToPortfolioMap map;
	
	public AssetClassToPortfolioMapWriteResponse(Long result, String shortStatus, String longStatus, AssetClassToPortfolioMap map) {
		super(result, shortStatus, longStatus);
		setMapAssetClassToPortfolio(map);
	}

	private void setMapAssetClassToPortfolio(AssetClassToPortfolioMap map) {
		this.map = map;
	}
	
	public AssetClassToPortfolioMap getMapAssetClassToPortfolio() {
		return map;
	}
}
