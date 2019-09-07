package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.model.entity.AssetClass;

public class AssetClassUpdateRequest extends AssetClassWriteRequest {

    public AssetClassUpdateRequest(Long requestorUserId, AssetClass assetClass) {
    	super (requestorUserId, assetClass);
    }
}