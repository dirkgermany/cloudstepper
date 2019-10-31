package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

public class StockQuotationDetail {
	private LocalDate startDate;
	private LocalDate endDate;
	
	private DayOfWeek dayOfWeek;
	private Month monthOfYear;
	private int year;
	private Float open = 0F;
	private Float close = 0F;
	private Float performancePercent = 0F;
	
	private Long stockHistoryId;

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Month getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(Month monthOfYear) {
		this.monthOfYear = monthOfYear;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Float getPerformancePercent() {
		return performancePercent;
	}

	public void setPerformancePercent(Float performancePercent) {
		this.performancePercent = performancePercent;
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

	public Long getStockHistoryId() {
		return stockHistoryId;
	}

	public void setStockHistoryId(Long stockHistoryId) {
		this.stockHistoryId = stockHistoryId;
	}
}
