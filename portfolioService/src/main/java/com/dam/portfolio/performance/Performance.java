package com.dam.portfolio.performance;

import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.Map;

public class Performance {
	
	private Map<Month, StockQuotationDetail> monthlyDetails;
	private Map<Year, StockQuotationDetail> yearDetails;
	private Map<Date, StockQuotationDetail> dailyDetails;
	
	private Date startDate;
	private Date endDate;
	
	private Float open;
	private Float close;
	private Float performancePercentage;

	public Map<Month, StockQuotationDetail> getMonthlyDetails() {
		return monthlyDetails;
	}

	public void setMonthlyDetails(Map<Month, StockQuotationDetail> monthlyDetails) {
		this.monthlyDetails = monthlyDetails;
	}

	public Map<Year, StockQuotationDetail> getYearDetails() {
		return yearDetails;
	}

	public void setYearDetails(Map<Year, StockQuotationDetail> yearDetails) {
		this.yearDetails = yearDetails;
	}

	public Map<Date, StockQuotationDetail> getDailyDetails() {
		return dailyDetails;
	}

	public void setDailyDetails(Map<Date, StockQuotationDetail> dailyDetails) {
		this.dailyDetails = dailyDetails;
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

	public Float getOpen() {
		return open;
	}

	public void setOpen(Float open) {
		this.open = open;
	}

	public Float getClose() {
		return close;
	}

	public void setClose(Float close) {
		this.close = close;
	}

	public Float getPerformancePercentage() {
		return performancePercentage;
	}

	public void setPerformancePercentage(Float performancePercentage) {
		this.performancePercentage = performancePercentage;
	}
}
