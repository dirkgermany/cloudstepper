package com.dam.stock.task.jobs;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dam.exception.DamServiceException;
import com.dam.stock.messageClass.AssetClass;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.store.StockHistoryStore;

/**
 * Charge money from house bank. Send Update Requests to ServiceProvider. Works
 * for Invest Intents and Deposit Intents (is the same step).
 * 
 * @author dirk
 *
 */
@Component
public class JobUpdateStocks extends Job {

	@Autowired
	AlphaVantageClient alphaVantageProvider;

	@Autowired
	ServiceProviderClient serviceProviderClient;
	
	@Autowired
	StockHistoryStore stockHistoryStore;

	public JobUpdateStocks() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		serviceProviderClient.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		// 1. Liste der Assets vom ServiceProvider anfordern
		List<AssetClass> assetClassList = serviceProviderClient.getAssetClassList();

		// 2. Je Asset die Stock-Liste abholen
		Iterator<AssetClass> it = assetClassList.iterator();
		while (it.hasNext()) {
			AssetClass asset = it.next();
			if (null != asset.getSymbol()) {
				Iterator<StockHistory> itStockHistory = alphaVantageProvider.getStockData(asset.getSymbol(), asset.getWkn()).iterator();
				
				// last date for asset
				StockHistory newestWrittenEntry = stockHistoryStore.findLastEntryForAsset(asset.getSymbol());

				while (itStockHistory.hasNext()) {
					StockHistory entry = itStockHistory.next();
					if (null == newestWrittenEntry || entry.getHistoryDate().isAfter(newestWrittenEntry.getHistoryDate())) {
						stockHistoryStore.storeStockHistory(entry);
					}
				}
			}
		}
	}

	@Override
	public void login(String className) throws DamServiceException {
		serviceProviderClient.login(className);
	}

	@Override
	public void logout(String className) throws DamServiceException {
		serviceProviderClient.logout(className);
	}
}
