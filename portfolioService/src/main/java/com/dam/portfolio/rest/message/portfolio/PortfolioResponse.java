package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestResponse;

public class PortfolioResponse extends RestResponse{
    private Portfolio construction;
    	
	public PortfolioResponse (Portfolio portfolio) {
		super(new Long(200), "OK", "Portfolio found");
		
		setPortfolio(construction);
	}  

	private void setPortfolio(Portfolio construction) {
		this.construction = construction;
	}
	
	public Portfolio getPortfolio() {
		return this.construction;
	}
	
}
