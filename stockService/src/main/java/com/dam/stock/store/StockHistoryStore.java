package com.dam.stock.store;

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
	
	public List<StockHistory> getStockHistorySafe(StockHistoryRequest historyRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(historyRequest, historyRequest.getRequestorUserId(),
				historyRequest.getRights());
		PermissionCheck.isReadPermissionSet(historyRequest.getRequestorUserId(), null, historyRequest.getRights());
		
		return null;

//		return getHistoryListBySymbol(historyRequest.getStockHistory().getSymbol());
	}

	public List<StockHistory> getHistoryListBySymbol(String symbol) {
		return stockHistoryModel.findAllBySymbol(symbol);
	}
	
	public StockHistory saveAccountStatus(StockHistory stockHistory) throws DamServiceException {
		return stockHistoryModel.save(stockHistory);
	}
}
