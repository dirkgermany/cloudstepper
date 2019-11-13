package com.dam.depot.rest.message.performance;

import java.time.LocalDate;

import com.dam.depot.rest.message.RestRequest;

public class DepotPerformanceRequest extends RestRequest {
	private Long userId;
    private Long portfolioId;
    private LocalDate startDate;
    private LocalDate endDate;

    public DepotPerformanceRequest(Long userId, Long portfolioId, LocalDate startDate, LocalDate endDate) {
		super("DAM 2.0");
		setPortfolioId(portfolioId);
		setUserId(userId);
		setStartDate(startDate);
		setEndDate(endDate);
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}