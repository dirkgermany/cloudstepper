package com.dam.stock.task.jobs;

import java.time.LocalDate;

import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.task.Client;

public abstract class ExternalAPIClient extends Client {

	/**
	 * Some data provider or contracts have limits (e.g. services for free). Some
	 * allow only a fix number of requests per minute/hour/day etc.
	 */
	protected abstract void waitTimeForNextRequest();

	/**
	 * Checks if the last stock history entry in db is up to date. Depending to the
	 * Weekday it makes sense or not to lookup for new Data. Considers null values;
	 * than the data also is not up to date.
	 */
	protected boolean isLastEntryUpToDate(StockHistory lastEntryForAssetInDB) {
		if (null == lastEntryForAssetInDB) {
			return false;
		}

		LocalDate today = LocalDate.now();

		switch (today.getDayOfWeek()) {
		case TUESDAY:
		case WEDNESDAY:
		case THURSDAY:
		case FRIDAY:
		case SATURDAY:
			if (lastEntryForAssetInDB.getHistoryDate().isBefore(today.minusDays(1))) {
				return false;
			}
			break;
			
		case SUNDAY:
			if (lastEntryForAssetInDB.getHistoryDate().isBefore(today.minusDays(2))) {
				return false;
			}
			break;
			
		case MONDAY:
			if (lastEntryForAssetInDB.getHistoryDate().isBefore(today.minusDays(3))) {
				return false;
			}
			break;
		}
		
		return true;

	}

	protected boolean isProviderDataNewest(StockHistory dataProviderHistoryEntry, StockHistory lastEntryForAssetInDB) {
		if (null == lastEntryForAssetInDB) {
			return true;
		}
		if (dataProviderHistoryEntry.getHistoryDate().isAfter(lastEntryForAssetInDB.getHistoryDate())
				|| (dataProviderHistoryEntry.getHistoryDate().isEqual(lastEntryForAssetInDB.getHistoryDate())
						&& (!dataProviderHistoryEntry.getOpen().equals(lastEntryForAssetInDB.getOpen())
								|| !dataProviderHistoryEntry.getClose().equals(lastEntryForAssetInDB.getClose())))) {
			return true;
		}
		return false;
	}

}
