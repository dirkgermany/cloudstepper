package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;

public class PortfolioCreateRequest extends PortfolioWriteRequest {

    public PortfolioCreateRequest(Long requestorUserId, Portfolio portfolio) {
		super(requestorUserId, portfolio);
    }
    
}