package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;

public class PortfolioCreateResponse extends PortfolioWriteResponse{

	public PortfolioCreateResponse(Portfolio portfolio) {
		super(new Long(200), "OK", "Portfolio Saved", portfolio);
	}
    	
}
