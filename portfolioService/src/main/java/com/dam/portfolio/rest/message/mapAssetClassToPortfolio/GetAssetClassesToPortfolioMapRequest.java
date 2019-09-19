package com.dam.portfolio.rest.message.mapAssetClassToPortfolio;

import com.dam.portfolio.rest.message.RestRequest;

public class GetAssetClassesToPortfolioMapRequest extends RestRequest {
	
	private Long portfolioId;

    public GetAssetClassesToPortfolioMapRequest(Long requestorUserId, Long portfolioId) {
		super("DAM 2.0");
		setRequestorUserId(requestorUserId);
		setPortfolioId(portfolioId);
    }

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

}