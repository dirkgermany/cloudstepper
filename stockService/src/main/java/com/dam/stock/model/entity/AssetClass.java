package com.dam.stock.model.entity;

import com.dam.stock.types.AssetClassType;

public class AssetClass {	

	private Long assetClassId;
	private String assetClassName;
	private Long providerId;
	private String providerName;
	private AssetClassType assetClassType;
	private String description;
	private String wkn;
	private String symbol;
	private String link;

	public AssetClass () {	
	}
	
	public String getAssetClassName() {
		return assetClassName;
	}

	public void setAssetClassName(String assetClassName) {
		this.assetClassName = assetClassName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public AssetClassType getAssetClassType() {
		return assetClassType;
	}

	public void setAssetClassType(AssetClassType assetClassType) {
		this.assetClassType = assetClassType;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
}
