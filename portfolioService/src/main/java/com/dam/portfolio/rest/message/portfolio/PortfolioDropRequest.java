package com.dam.portfolio.rest.message.portfolio;

import com.dam.portfolio.model.entity.Portfolio;

public class PortfolioDropRequest extends PortfolioWriteRequest {

	public PortfolioDropRequest(Long requestorUserId, Portfolio portfolio) {
		super(requestorUserId, portfolio);
    }
}