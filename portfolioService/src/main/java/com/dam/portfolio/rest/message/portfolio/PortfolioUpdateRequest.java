package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;

public class PortfolioUpdateRequest extends PortfolioWriteRequest {

    public PortfolioUpdateRequest(Long requestorUserId, Portfolio portfolio) {
    	super (requestorUserId, portfolio);
    }
}