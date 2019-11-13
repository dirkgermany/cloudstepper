package com.dam.depot.performance;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class DepotPerformanceDetail {
	private LocalDate startDate;
	private LocalDate endDate;
	private Float open;
	private Float close;
	private Float performancePercent;

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

	public Float getDepotValue() {
		return getClose()*(1+getPerformancePercent());
	}

}
