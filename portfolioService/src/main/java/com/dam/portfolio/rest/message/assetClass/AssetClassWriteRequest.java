package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.rest.message.RestRequest;

public abstract class AssetClassWriteRequest extends RestRequest {

	private AssetClass assetClass = new AssetClass();	

    public AssetClassWriteRequest(Long requestorUserId, AssetClass assetClass) {
		super("DAM 2.0");
		setAssetClass(assetClass);
		setRequestorUserId(requestorUserId);
    }
        
    public AssetClass getAssetClass() {
    	return assetClass;
    }

	public void setAssetClass(AssetClass assetClass) {
		this.assetClass = assetClass;
	}
    
}