package com.dam.depot.rest.message.performance;

import java.time.LocalDate;

import com.dam.depot.rest.message.RestRequest;

public class PortfolioPerformanceRequest extends RestRequest {
    private Long portfolioId;
    private Long assetClassId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean showPortfolioPerformance = false;
    private boolean showAllAssetsOfPortfolio = false;
    private boolean showAssetPerformance = false;

    public PortfolioPerformanceRequest(Long requestorUserId, Long portfolioId, Long assetClassId, LocalDate startDate, LocalDate endDate, boolean showPortfolioPerformance, boolean showAllAssetsOfPortfolio, boolean showAssetPerformance) {
		super("DAM 2.0");
		setPortfolioId(portfolioId);
		setRequestorUserId(requestorUserId);
		setStartDate(startDate);
		setEndDate(endDate);
		setAssetClassId(assetClassId);
		setShowPortfolioPerformance(showPortfolioPerformance);
		setShowAllAssetsOfPortfolio(showAllAssetsOfPortfolio);
		setShowAssetPerformance(showAssetPerformance);
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

	public boolean isShowPortfolioPerformance() {
		return showPortfolioPerformance;
	}

	public void setShowPortfolioPerformance(boolean showPortfolioPerformance) {
		this.showPortfolioPerformance = showPortfolioPerformance;
	}

	public boolean isShowAllAssetsOfPortfolio() {
		return showAllAssetsOfPortfolio;
	}

	public void setShowAllAssetsOfPortfolio(boolean showAllAssetsOfPortfolio) {
		this.showAllAssetsOfPortfolio = showAllAssetsOfPortfolio;
	}

	public boolean isShowAssetPerformance() {
		return showAssetPerformance;
	}

	public void setShowAssetPerformance(boolean showAssetPerformance) {
		this.showAssetPerformance = showAssetPerformance;
	}
    
}