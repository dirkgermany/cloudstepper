package com.dam.portfolio.rest.message.assetClass;

import java.util.List;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.rest.message.RestResponse;

public class AssetClassListResponse extends RestResponse {
	
	private List<AssetClass> assetClassList;

	public AssetClassListResponse(List<AssetClass> assetClasses) {
		super(new Long(200), "OK", "AssetClasses found");
		this.assetClassList = assetClasses;
	}

	public List<AssetClass> getAssetClassList() {
		return assetClassList;
	}

	public void setAssetClassList(List<AssetClass> assetClassList) {
		this.assetClassList = assetClassList;
	}

}
