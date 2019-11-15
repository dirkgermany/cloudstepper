package com.dam.stock.store;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.stock.PermissionCheck;
import com.dam.stock.model.StockHistoryModel;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.message.stock.StockHistoryRequest;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class StockHistoryStore {

	@Autowired
	private StockHistoryModel stockHistoryModel;

	public long count() {
		return stockHistoryModel.count();
	}
	
	public StockHistory findLastEntryForAsset (String symbol) throws DamServiceException {
		return stockHistoryModel.findLastEntryForAsset (symbol);
	}

	public List<StockHistory> getStockHistorySafe(StockHistoryRequest historyRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(historyRequest, historyRequest.getRequestorUserId(),
				historyRequest.getRights());
		PermissionCheck.isReadPermissionSet(historyRequest.getRequestorUserId(), null, historyRequest.getRights());

		return getHistoryList(historyRequest);
	}

	public List<StockHistory> getHistoryList(StockHistoryRequest historyRequest) {
		if (null != historyRequest.getStockHistory().getSymbol()) {
			return stockHistoryModel.findBySymbolStartEndDate(historyRequest.getStockHistory().getSymbol(), historyRequest.getFilterStartDate(), historyRequest.getFilterEndDate());
		}
		else {
			return new ArrayList<StockHistory>();
		}
	}

	// makes an update if entity exists
	public StockHistory storeStockHistory(StockHistory stockHistory) throws DamServiceException {
		StockHistory storedEntry = stockHistoryModel.findBySymbolDate(stockHistory.getSymbol(),	stockHistory.getHistoryDate());
		if (null != storedEntry) {
			storedEntry.updateEntity(stockHistory);
			return stockHistoryModel.save(storedEntry);
		}
		return stockHistoryModel.save(stockHistory);
	}
}
