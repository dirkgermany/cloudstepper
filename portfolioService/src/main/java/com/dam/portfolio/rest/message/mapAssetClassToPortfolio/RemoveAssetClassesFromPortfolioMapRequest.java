package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import java.util.List;

import com.dam.portfolio.rest.message.RestRequest;

public class RemoveAssetClassesFromPortfolioMapRequest extends RestRequest {
	
	private Long portfolioId;
	private List<Long> assetClassIds;

    public RemoveAssetClassesFromPortfolioMapRequest(Long requestorUserId, Long portfolioId, List<Long> assetClassIds) {
		super("DAM 2.0");
		setRequestorUserId(requestorUserId);
		setPortfolioId(portfolioId);
		setAssetClassIds(assetClassIds);
    }

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public List<Long> getAssetClassIds() {
		return assetClassIds;
	}

	public void setAssetClassIds(List<Long> assetClassIds) {
		this.assetClassIds = assetClassIds;
	}
    
}