package com.dam.stock.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.stock.rest.message.RestResponse;
import com.dam.stock.rest.message.stock.StockHistoryRequest;
import com.dam.stock.rest.message.stock.StockHistoryResponse;
import com.dam.stock.store.StockHistoryStore;

@RestController
public class StockHistoryController {
	@Autowired
	private StockHistoryStore stockHistoryStore;

	@PostMapping("/getStockHistory")
	public RestResponse getAccountStatus (@RequestBody StockHistoryRequest stockHistoryRequest) throws DamServiceException {
		try {
			return new StockHistoryResponse(stockHistoryStore.getStockHistorySafe(stockHistoryRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}