package com.dam.portfolio.rest.message.portfolio;

import java.util.Date;

import com.dam.portfolio.rest.message.RestRequest;

public class PortfolioPerformanceRequest extends RestRequest {
    private Long portfolioId;
    private Long assetClassId;
    private Date startDate;
    private Date endDate;

    public PortfolioPerformanceRequest(Long requestorUserId, Long portfolioId, Long assetClassId, Date startDate, Date endDate) {
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

	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
    
}