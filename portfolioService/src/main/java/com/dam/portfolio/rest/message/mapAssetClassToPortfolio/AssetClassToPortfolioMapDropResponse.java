package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.rest.message.RestResponse;

public class AssetClassToPortfolioMapDropResponse extends RestResponse{
    	
	public AssetClassToPortfolioMapDropResponse (Long result) {
		super(result, "OK", "MapEntry dropped");
	}  
}
