package com.dam.portfolio.rest.message.portfolio;

import java.util.Date;

import com.dam.portfolio.rest.message.RestRequest;
import com.dam.portfolio.types.PerformanceInterval;

public class PortfolioRequest extends RestRequest {
    private Long portfolioId;
    private Date startDate = new Date(0);
    private Date endDate = new Date(Long.MAX_VALUE);
    private PerformanceInterval performanceInterval;

    public PortfolioRequest( Long requestorUserId, Long portfolioId, Date startDate, Date endDate, PerformanceInterval performanceInterval) {
		super("DAM 2.0");
		
		setPortfolioId(portfolioId);
		setRequestorUserId(requestorUserId);
		
		if (null != startDate) {
			setStartDate(startDate);
		}
		
		if (null != endDate) {
			setEndDate(endDate);
		}
		
		setPerformanceInterval(performanceInterval);
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
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

	public PerformanceInterval getPerformanceInterval() {
		return performanceInterval;
	}

	public void setPerformanceInterval(PerformanceInterval performanceInterval) {
		this.performanceInterval = performanceInterval;
	}
    
}