package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.rest.message.RestResponse;

public abstract class AssetClassWriteResponse extends RestResponse{
    private AssetClass assetClass;
	
	public AssetClassWriteResponse(Long result, String shortStatus, String longStatus, AssetClass assetClass) {
		super(result, shortStatus, longStatus);
		setAssetClass(assetClass);
	}

	private void setAssetClass(AssetClass assetClass) {
		this.assetClass = assetClass;
	}
	
	public AssetClass getAssetClass() {
		return assetClass;
	}
}
