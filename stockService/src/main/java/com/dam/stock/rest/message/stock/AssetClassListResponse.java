package com.dam.stock.rest.message.stock;

import java.util.List;

import com.dam.stock.messageClass.AssetClass;
import com.dam.stock.rest.message.RestResponse;

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
