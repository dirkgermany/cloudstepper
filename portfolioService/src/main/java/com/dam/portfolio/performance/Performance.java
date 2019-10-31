package com.dam.portfolio.performance;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Performance {
	
	private Map<Month, StockQuotationDetail> monthlyDetails = new HashMap<>();
	private Map<Integer, StockQuotationDetail> yearDetails = new HashMap<>();
	private Map<LocalDate, StockQuotationDetail> dailyDetails = new TreeMap<>();
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	private Float open;
	private Float close;
	private Float performancePercentage;

	public Map<Month, StockQuotationDetail> getMonthlyDetails() {
		return monthlyDetails;
	}

	public void setMonthlyDetails(Map<Month, StockQuotationDetail> monthlyDetails) {
		this.monthlyDetails = monthlyDetails;
	}

	public Map<Integer, StockQuotationDetail> getYearDetails() {
		return yearDetails;
	}

	public void setYearDetails(Map<Integer, StockQuotationDetail> yearDetails) {
		this.yearDetails = yearDetails;
	}

	public Map<LocalDate, StockQuotationDetail> getDailyDetails() {
		return dailyDetails;
	}

	public void setDailyDetails(Map<LocalDate, StockQuotationDetail> dailyDetails) {
		this.dailyDetails = dailyDetails;
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
