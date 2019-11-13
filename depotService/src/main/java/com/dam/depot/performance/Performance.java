package com.dam.depot.performance;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.util.Precision;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Performance {

	private Map<Month, StockQuotationDetail> monthlyDetails = new HashMap<>();
	private Map<Integer, StockQuotationDetail> yearDetails = new HashMap<>();
	private Map<LocalDate, StockQuotationDetail> dailyDetails = new TreeMap<>();

	private LocalDate startDate;
	private LocalDate endDate;

	private Float open;
	private Float close;

	public void addToOpen(Float value) {
		if (null == this.open) {
			this.open = 0F;
		}
		this.open += value;
	}

	public void addToClose(Float value) {
		if (null == this.close) {
			this.close = 0F;
		}
		this.close += value;
	}

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

	@JsonIgnore
	public Float getPerformancePercent() {
		if (null == this.open || null == this.close) {
			return 0F;
		}
		
		float calculated = ((this.close/this.open - 1) * 100);
		return Precision.round(calculated, 6);
	}
	
	@JsonIgnore
	public String getPerformanceAsString() {
		DecimalFormat formatter = new DecimalFormat("#,##0.00'%'");
		formatter.setMultiplier(1);

		return formatter.format(getPerformancePercent());
	}

}
