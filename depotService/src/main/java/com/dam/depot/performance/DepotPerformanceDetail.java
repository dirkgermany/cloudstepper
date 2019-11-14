package com.dam.depot.performance;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class DepotPerformanceDetail {
	private LocalDate startDate;
	private LocalDate endDate;
	private Float invest;
	private Float investAtAll;
	private Float amountAtBegin;
//	private Float amountAtEnd;
	private Float performancePercent;
	private Boolean marketOpen = true;

	public void addToInvest(Float value) {
		if (null == this.invest) {
			this.invest = 0F;
		}
		invest += value;
	}

	public Float getAmountAtBegin() {
		return amountAtBegin;
	}

	public void setAmountAtBegin(Float open) {
		this.amountAtBegin = open;
	}

	public void setPerformanceAsString(String performanceAsString) {
		// nothing to do
	}
	
	public String getPerformanceAsString() {
		DecimalFormat formatter = new DecimalFormat("#,##0.00'%'");
		formatter.setMultiplier(1);

		return formatter.format(getPerformancePercent());
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

	public Float getPerformancePercent() {
		return performancePercent;
	}

	public void setPerformancePercent(Float performancePercent) {
		this.performancePercent = performancePercent;
	}
	
	public Float getAmountAtEnd() {
		return (getAmountAtBegin() + getInvest()) * (1+(getPerformancePercent()/100));
	}

	public Float getInvest() {
		return invest == null ? 0F : invest;
	}

	public void setInvest(Float invest) {
		this.invest = invest;
	}

	public Boolean getMarketOpen() {
		return marketOpen;
	}

	public void setMarketOpen(Boolean marketOpen) {
		this.marketOpen = marketOpen;
	}

	public Float getInvestAtAll() {
		return investAtAll;
	}

	public void setInvestAtAll(Float investAtAll) {
		this.investAtAll = investAtAll;
	}

}
