package com.dam.portfolio.performance;

public class AssetPerformance extends Performance {

	Long assetClassId;
	String assetClassName;
	
	public Long getAssetClassId() {
		return assetClassId;
	}
	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}
	public String getAssetClassName() {
		return assetClassName;
	}
	public void setAssetClassName(String assetClassName) {
		this.assetClassName = assetClassName;
	}

}
