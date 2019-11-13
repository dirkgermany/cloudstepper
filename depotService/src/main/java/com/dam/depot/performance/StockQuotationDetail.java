package com.dam.depot.performance;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

import org.apache.commons.math3.util.Precision;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StockQuotationDetail {
	private LocalDate startDate;
	private LocalDate endDate;

	private DayOfWeek dayOfWeek;
	private Month monthOfYear;
	private int year;
	private Float open;
	private Float close;
	private Float openWeighted;
	private Float closeWeighted;
	private Long stockHistoryId;

	public void addToOpen(Float value) {
		if (null == this.open) {
			this.open = 0F;
		}
		open += value;
	}

	public void addToClose(Float value) {
		if (null == this.close) {
			this.close = 0F;
		}
		close += value;
	}

	public void addToOpenWeighted(Float value) {
		if (null == this.openWeighted) {
			this.openWeighted = 0F;
		}
		openWeighted += value;
	}

	public void addToCloseWeighted(Float value) {
		if (null == this.closeWeighted) {
			this.closeWeighted = 0F;
		}
		closeWeighted += value;
	}

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

	public Float getOpenWeighted() {
		return openWeighted;
	}

	public void setOpenWeighted(Float openWeighted) {
		this.openWeighted = openWeighted;
	}

	public Float getCloseWeighted() {
		return closeWeighted;
	}

	public void setCloseWeighted(Float closeWeighted) {
		this.closeWeighted = closeWeighted;
	}
}
