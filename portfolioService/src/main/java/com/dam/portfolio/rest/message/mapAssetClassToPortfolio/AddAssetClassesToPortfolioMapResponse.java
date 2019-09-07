package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;

public class AddAssetClassesToPortfolioMapResponse extends RestResponse{
	
	private ConstructionMap map;

	public AddAssetClassesToPortfolioMapResponse(ConstructionMap map) {
		super (new Long(200), "OK", "Assets Added");
		setMap(map);
	}

	public ConstructionMap getMap() {
		return map;
	}

	public void setMap(ConstructionMap map) {
		this.map = map;
	}

}
