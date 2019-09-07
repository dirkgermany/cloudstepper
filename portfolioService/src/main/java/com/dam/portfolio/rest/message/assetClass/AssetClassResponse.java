package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.rest.message.RestResponse;

public class AssetClassResponse extends RestResponse{
    private AssetClass assetClass;
    	
	public AssetClassResponse (AssetClass assetClass) {
		super(new Long(200), "OK", "AssetClass found");
		
		setAssetClass(assetClass);
	}  

	private void setAssetClass(AssetClass assetClass) {
		this.assetClass = assetClass;
	}
	
	public AssetClass getAssetClass() {
		return this.assetClass;
	}
	
}
