package com.dam.portfolio.performance;

import java.util.HashMap;
import java.util.Map;

public class ClassTypeValues {
	
	private Float open;
	private Float close;
	private Float openWeighted;
	private Float closeWeighted;
	private Map<String, AssetClassValues> assetClassValues = new HashMap<>();
	
	public Float getOpen() {
		return open;
	}
	public void setOpen(Float open) {
		this.open = open;
	}
	public Float getClose() {
		return close;
	}
	public void setClose(Float close) {
		this.close = close;
	}
	
	public void addToOpen(Float value) {
		if (null == this.open) {
			this.open = 0F;
		}
		this.open += value;
	}

	public void addToClose(Float value) {
		if (null == this.close) {
			this.close = 0F;
		}
		this.close += value;
	}
	public void addToOpenWeighted(Float value) {
		if (null == this.openWeighted) {
			this.openWeighted = 0F;
		}
		this.openWeighted += value;
	}

	public void addToCloseWeighted(Float value) {
		if (null == this.closeWeighted) {
			this.closeWeighted = 0F;
		}
		this.closeWeighted += value;
	}
	public Map<String, AssetClassValues> getAssetClassValues() {
		return assetClassValues;
	}
	public void setAssetClassValues(Map<String, AssetClassValues> assetClassValues) {
		this.assetClassValues = assetClassValues;
	}
	public Float getOpenWeighted() {
		return openWeighted;
	}
	public void setOpenWeighted(Float openWeighted) {
		this.openWeighted = openWeighted;
	}
	public Float getCloseWeighted() {
		return closeWeighted;
	}
	public void setCloseWeighted(Float closeWeighted) {
		this.closeWeighted = closeWeighted;
	}
}
