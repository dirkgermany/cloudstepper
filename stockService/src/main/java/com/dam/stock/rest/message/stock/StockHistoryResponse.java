package com.dam.stock.rest.message.stock;

import java.util.List;

import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.message.RestResponse;

public class StockHistoryResponse extends RestResponse {

	private List<StockHistory> stockHistoryList;

	public StockHistoryResponse( List<StockHistory> stockHistoryList) {
		super(new Long(200), "OK", "Reqest performed");
		setStockHistoryList(stockHistoryList);
	}

	public List<StockHistory> getStockHistoryList() {
		return stockHistoryList;
	}

	public void setStockHistoryList(List<StockHistory> stockHistoryList) {
		this.stockHistoryList = stockHistoryList;
	}
}
