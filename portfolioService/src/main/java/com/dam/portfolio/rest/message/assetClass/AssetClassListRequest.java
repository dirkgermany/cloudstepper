package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.types.AssetClassType;

public class AssetClassListRequest extends AssetClassRequest {
	
	private AssetClassType assetClassType;

	public AssetClassListRequest(Long requestorUserId, Long assetClassId) {
		super(requestorUserId, assetClassId);
	}

	public AssetClassType getAssetClassType() {
		return assetClassType;
	}

	public void setAssetClassType(AssetClassType assetClassType) {
		this.assetClassType = assetClassType;
	}

}
