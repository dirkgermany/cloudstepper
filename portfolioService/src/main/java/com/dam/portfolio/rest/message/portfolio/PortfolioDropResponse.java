package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.rest.message.RestResponse;

public class PortfolioDropResponse extends RestResponse{
    	
	public PortfolioDropResponse (Long result) {
		super(result, "OK", "Portfolio dropped");
	}  
}
