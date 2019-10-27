package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.rest.message.RestRequest;

public class PortfolioPerformanceRequest extends RestRequest {
    private Long portfolioId;

    public PortfolioPerformanceRequest( Long requestorUserId, Long portfolioId) {
		super("DAM 2.0");
		setPortfolioId(portfolioId);
		setRequestorUserId(requestorUserId);
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
    
}