package com.dam.stock.rest.message.stock;

import java.util.Date;

import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.message.RestRequest;

public class StockHistoryRequest extends RestRequest {

	private Date filterStartDate = new Date(0);
	private Date filterEndDate = new Date(Long.MAX_VALUE);
	private StockHistory stockHistory;

	public StockHistoryRequest(StockHistory stockHistory, Date filterStartDate, Date filterEndDate) {
		super("DAM 2.0");
		setStockHistory(stockHistory);

		if (null != filterStartDate) {
			setFilterStartDate(filterStartDate);
		}
		if (null != filterEndDate) {
			setFilterEndDate(filterEndDate);
		}
	}

	public StockHistory getStockHistory() {
		return stockHistory;
	}

	public void setStockHistory(StockHistory stockHistory) {
		this.stockHistory = stockHistory;
	}

	public Date getFilterStartDate() {
		return filterStartDate;
	}

	public void setFilterStartDate(Date filterStartDate) {
		this.filterStartDate = filterStartDate;
	}

	public Date getFilterEndDate() {
		return filterEndDate;
	}

	public void setFilterEndDate(Date filterEndDate) {
		this.filterEndDate = filterEndDate;
	}
}
