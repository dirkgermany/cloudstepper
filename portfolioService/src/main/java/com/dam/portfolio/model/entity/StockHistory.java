package com.dam.portfolio.model.entity;

import java.time.LocalDate;

import com.dam.portfolio.types.Symbol;

public class StockHistory {	

	private Long stockHistoryId;
	private Symbol symbol;
	private String wkn;
	private Float open;
	private Float close;
	private LocalDate historyDate;
	
	public StockHistory updateEntity (StockHistory container) {
		setSymbol(container.getSymbol());
		setWkn(container.getWkn());
		setOpen(container.getOpen());
		setClose(container.getClose());
		setHistoryDate(container.getHistoryDate());
		return this;
	}

	public Long getStockHistoryId() {
		return stockHistoryId;
	}

	public void setStockHistoryId(Long stockHistoryId) {
		this.stockHistoryId = stockHistoryId;
	}

	public com.dam.portfolio.types.Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(com.dam.portfolio.types.Symbol symbol) {
		this.symbol = symbol;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
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

	public LocalDate getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(LocalDate historyDate) {
		this.historyDate = historyDate;
	}

}