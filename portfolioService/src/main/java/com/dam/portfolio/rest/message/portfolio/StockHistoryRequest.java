package com.dam.portfolio.rest.message.portfolio;

import java.time.LocalDate;

import com.dam.portfolio.model.entity.StockHistory;
import com.dam.portfolio.rest.message.RestRequest;

public class StockHistoryRequest extends RestRequest {

	private LocalDate filterStartDate;
	private LocalDate filterEndDate;
	private StockHistory stockHistory;
	
	public StockHistoryRequest(StockHistory stockHistory, LocalDate filterStartDate, LocalDate filterEndDate) {
		super("DAM 2.0");
		setStockHistory(stockHistory);

			setFilterStartDate(filterStartDate);
			setFilterEndDate(filterEndDate);
	}

	public StockHistory getStockHistory() {
		return stockHistory;
	}

	public void setStockHistory(StockHistory stockHistory) {
		this.stockHistory = stockHistory;
	}

	public LocalDate getFilterStartDate() {
		return filterStartDate;
	}

	public void setFilterStartDate(LocalDate filterStartDate) {
		this.filterStartDate = filterStartDate;
	}

	public LocalDate getFilterEndDate() {
		return filterEndDate;
	}

	public void setFilterEndDate(LocalDate filterEndDate) {
		this.filterEndDate = filterEndDate;
	}
}
