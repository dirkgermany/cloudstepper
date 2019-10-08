package com.dam.stock.rest.message.stock;

import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.message.RestRequest;

public class StockHistoryRequest extends RestRequest {
	
//	private StockHistory stockHistory;
	
    public StockHistoryRequest(StockHistory stockHistory) {
		super("DAM 2.0");
//		setStockHistory(stockHistory);
    }


//	public StockHistory getStockHistory() {
//		return stockHistory;
//	}
//
//	public void setStockHistory(StockHistory stockHistory) {
//		this.stockHistory = stockHistory;
//	}
}
