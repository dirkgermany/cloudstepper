package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.rest.message.RestRequest;

public class AssetClassRequest extends RestRequest {
    private Long assetClassId;

    public AssetClassRequest( Long requestorUserId, Long assetClassId) {
		super("DAM 2.0");
		setRequestorUserId(requestorUserId);
	}

	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}
    
}