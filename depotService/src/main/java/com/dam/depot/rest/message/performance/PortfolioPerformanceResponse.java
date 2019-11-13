package com.dam.depot.rest.message.performance;

import java.util.List;

import com.dam.depot.performance.AssetPerformance;
import com.dam.depot.performance.PortfolioPerformance;
import com.dam.depot.rest.message.RestResponse;

public class PortfolioPerformanceResponse extends RestResponse{
	
	private PortfolioPerformance portfolioPerformance;
	private AssetPerformance assetPerformance;
	private List<AssetPerformance> assetPerformanceList;
	

	public PortfolioPerformanceResponse(Long result, String shortStatus, String longStatus, PortfolioPerformance portfolioPerformance, AssetPerformance assetPerformance, List<AssetPerformance> assetPerformanceList) {
		super(result, shortStatus, longStatus);
		setPortfolioPerformance(portfolioPerformance);
		setAssetPerformance(assetPerformance);
		setAssetPerformanceList(assetPerformanceList);
	}


	public PortfolioPerformance getPortfolioPerformance() {
		return portfolioPerformance;
	}


	public void setPortfolioPerformance(PortfolioPerformance portfolioPerformance) {
		this.portfolioPerformance = portfolioPerformance;
	}


	public AssetPerformance getAssetPerformance() {
		return assetPerformance;
	}


	public void setAssetPerformance(AssetPerformance assetPerformance) {
		this.assetPerformance = assetPerformance;
	}


	public List<AssetPerformance> getAssetPerformanceList() {
		return assetPerformanceList;
	}


	public void setAssetPerformanceList(List<AssetPerformance> assetPerformanceList) {
		this.assetPerformanceList = assetPerformanceList;
	}
    	
}
