package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.Date;

public class StockQuotationDetail {
	private Date startDate;
	private Date endDate;
	
	private DayOfWeek dayOfWeek;
	private Month monthOfYear;
	private Year year;
	private Float open = 0F;
	private Float close = 0F;
	private Float performancePercent = 0F;

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

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Float getPerformancePercent() {
		return performancePercent;
	}

	public void setPerformancePercent(Float performancePercent) {
		this.performancePercent = performancePercent;
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
