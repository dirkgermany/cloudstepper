package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestRequest;

public abstract class PortfolioWriteRequest extends RestRequest {

	private Portfolio portfolio = new Portfolio();	

    public PortfolioWriteRequest(Long requestorUserId, Portfolio portfolio) {
		super("DAM 2.0");
		setPortfolio(portfolio);
		setRequestorUserId(requestorUserId);
    }
        
    public Portfolio getPortfolio() {
    	return portfolio;
    }

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
    
}