package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestResponse;

public abstract class PortfolioWriteResponse extends RestResponse{
    private Portfolio portfolio;
	
	public PortfolioWriteResponse(Long result, String shortStatus, String longStatus, Portfolio portfolio) {
		super(result, shortStatus, longStatus);
		setPortfolio(portfolio);
	}

	private void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
}