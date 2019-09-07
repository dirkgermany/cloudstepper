package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;
import com.dam.portfolio.rest.message.RestResponse;

public class AssetClassToPortfolioMapResponse extends RestResponse{
    private AssetClassToPortfolioMap map;
    	
	public AssetClassToPortfolioMapResponse (AssetClassToPortfolioMap map) {
		super(new Long(200), "OK", "MapEntry found");
		setMapAssetClassToPortfolio(map);
	}  

	private void setMapAssetClassToPortfolio(AssetClassToPortfolioMap map) {
		this.map = map;
	}
	
	public AssetClassToPortfolioMap getMapAssetClassToPortfolio() {
		return this.map;
	}
	
}
