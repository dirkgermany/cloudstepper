package com.dam.portfolio.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ConstructionMap {
	
	private List<AssetClass> assetClasses;
	private Portfolio portfolio;
	
	public ConstructionMap () {
		
	}
	
	public void addAssetClass(AssetClass assetClass) {
		if (null == assetClasses) {
			assetClasses = new ArrayList<>();
		}
		getAssetClasses().add(assetClass);
	}
	
	
	public ConstructionMap (Portfolio portfolio, List<AssetClass> assetClasses) {
		setAssetClasses(assetClasses);
		setPortfolio(portfolio);
	}

	public List<AssetClass> getAssetClasses() {
		return assetClasses;
	}

	public void setAssetClasses(List<AssetClass> assetClasses) {
		this.assetClasses = assetClasses;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

}
