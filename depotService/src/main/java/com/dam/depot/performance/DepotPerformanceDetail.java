package com.dam.depot.performance;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class DepotPerformanceDetail {
	private LocalDate startDate;
	private LocalDate endDate;
	private Float invest;
	private Float investAtAll;
	private Float openRate;
	private Float closeRate;
	private Float performancePercent;
	private Float performanceAtAll;

	private Boolean marketOpen = true;

	public void addToInvest(Float value) {
		if (null == this.invest) {
			this.invest = 0F;
		}
		invest += value;
	}

//	public void setPerformanceAsString(String performanceAsString) {
//		// nothing to do
//	}
//	
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
	
	public void addPerformanceAtAll(Float value) {
		if (null == performanceAtAll) {
			performanceAtAll = value;
		}
		else {
			performanceAtAll+= value;
		}
	}
	
	public Float getAmountAtEnd() {
		return getInvestAtAll() * (1+(getPerformancePercent()/100));
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
		return investAtAll == null ? 0F : investAtAll ;
	}

	public void setInvestAtAll(Float investAtAll) {
		this.investAtAll = investAtAll;
	}

	public Float getOpenRate() {
		return openRate;
	}

	public void setOpenRate(Float openRate) {
		this.openRate = openRate;
	}

	public Float getCloseRate() {
		return closeRate;
	}

	public void setCloseRate(Float closeRate) {
		this.closeRate = closeRate;
	}

	public Float getPerformanceAtAll() {
		return getAmountAtEnd() - getInvest();
	}
}
