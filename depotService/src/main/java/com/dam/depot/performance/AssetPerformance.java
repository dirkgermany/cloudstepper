package com.dam.depot.performance;

import com.dam.depot.types.AssetClassType;

public class AssetPerformance extends Performance {

	private Long assetClassId;
	private String assetClassName;
	private Float weighting;
	private AssetClassType assetClassType;
	private String symbol;
	
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
	public Float getWeighting() {
		return weighting;
	}
	public void setWeighting(Float weighting) {
		this.weighting = weighting;
	}
	public AssetClassType getAssetClassType() {
		return assetClassType;
	}
	public void setAssetClassType(AssetClassType assetClassType) {
		this.assetClassType = assetClassType;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
