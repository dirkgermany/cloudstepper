package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.model.entity.StockHistory;

@Component
public class PortfolioPerformanceCalculator {

	private static Map<Long, PortfolioPerformance> portfolioPerformanceMap = new HashMap<>();
	private static Map<Long, AssetPerformance> assetPerformanceMap = new HashMap<>();
	private static Map<Long, Long> processedStockDetailEntryMap = new HashMap<>();

	/**
	 * Summary over all asset details.
	 * 
	 * @param portfolioId
	 * @return
	 */
	public PortfolioPerformance getPortfolioPerformance(Long portfolioId, LocalDate startDate, LocalDate endDate) {
		PortfolioPerformance storedPerformance = portfolioPerformanceMap.get(portfolioId);
		
		PortfolioPerformance filteredPerformance = new PortfolioPerformance();
		filteredPerformance.setOpen(storedPerformance.getOpen());
		filteredPerformance.setClose(storedPerformance.getClose());
		filteredPerformance.setStartDate(endDate);
		filteredPerformance.setEndDate(startDate);
		filteredPerformance.setPortfolioId(portfolioId);
		filteredPerformance.setPortfolioName(storedPerformance.getPortfolioName());
		
		Iterator<Map.Entry<LocalDate, StockQuotationDetail>> it = storedPerformance.getDailyDetails().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<LocalDate, StockQuotationDetail> entry = it.next();
			if ((entry.getKey().isEqual(startDate) || entry.getKey().isAfter(startDate)) && (entry.getKey().isEqual(endDate) || entry.getKey().isBefore(endDate))) {
				if (filteredPerformance.getStartDate().isAfter(entry.getKey())) {
					filteredPerformance.setOpen(entry.getValue().getOpen());
					filteredPerformance.setStartDate(entry.getKey());
				}
				if (filteredPerformance.getEndDate().isBefore(entry.getKey())) {
					filteredPerformance.setClose(entry.getValue().getClose());
					filteredPerformance.setEndDate(entry.getKey());
				}

				filteredPerformance.getDailyDetails().put(entry.getKey(), entry.getValue());
			}
		}
	
		return filteredPerformance;
//		return portfolioPerformanceMap.get(portfolioId);
	}

	public Map<Long, AssetPerformance> getAssetPerformanceMap(LocalDate startDate, LocalDate endDate) {
		return assetPerformanceMap;
	}
	
	public AssetPerformance getAssetPerformance(Long assetClassId, LocalDate startDate, LocalDate endDate) {
		Iterator<AssetPerformance> it = assetPerformanceMap.values().iterator();
		while (it.hasNext()) {
			AssetPerformance storedPerformance = it.next();
			if (storedPerformance.getAssetClassId().equals(assetClassId)) {
				AssetPerformance filteredPerformance = filterAssetPerformance(storedPerformance, startDate, endDate);
				return filteredPerformance;
			}
		}
		return null;
	}

	private AssetPerformance filterAssetPerformance(AssetPerformance storedPerformance, LocalDate startDate, LocalDate endDate) {
		AssetPerformance filteredPerformance = new AssetPerformance();
		
		filteredPerformance.setOpen(storedPerformance.getOpen());
		filteredPerformance.setClose(storedPerformance.getClose());
		filteredPerformance.setStartDate(endDate);
		filteredPerformance.setEndDate(startDate);
		filteredPerformance.setAssetClassId(storedPerformance.getAssetClassId());
		filteredPerformance.setAssetClassName(storedPerformance.getAssetClassName());
		
		Iterator<Map.Entry<LocalDate, StockQuotationDetail>> itDetails = storedPerformance.getDailyDetails().entrySet().iterator();
		while (itDetails.hasNext()) {
			Map.Entry<LocalDate, StockQuotationDetail> entry = itDetails.next();
			if ((entry.getKey().isEqual(startDate) || entry.getKey().isAfter(startDate)) && (entry.getKey().isEqual(endDate) || entry.getKey().isBefore(endDate))) {
				if (filteredPerformance.getStartDate().isAfter(entry.getKey())) {
					filteredPerformance.setOpen(entry.getValue().getOpen());
					filteredPerformance.setStartDate(entry.getKey());
				}
				if (filteredPerformance.getEndDate().isBefore(entry.getKey())) {
					filteredPerformance.setClose(entry.getValue().getClose());
					filteredPerformance.setEndDate(entry.getKey());
				}
				filteredPerformance.getDailyDetails().put(entry.getKey(), entry.getValue());
			}
		}
		return filteredPerformance;
	}
	
	public List<AssetPerformance> getAssetPerformanceList(LocalDate startDate, LocalDate endDate) {
		List<AssetPerformance> performanceList = new ArrayList<>();
		
		Iterator<AssetPerformance> it = assetPerformanceMap.values().iterator();
		while (it.hasNext()) {
			AssetPerformance storedPerformance = it.next();
			AssetPerformance filteredPerformance = 	filterAssetPerformance(storedPerformance,  startDate,  endDate);
			performanceList.add(filteredPerformance);
		}
		return performanceList;
	}

	/*
	 * Must be called at first. Initializes the performance values. The performance
	 * values are stored static.
	 */
	public void generatePerformanceLists(Portfolio portfolio, Map<AssetClass, List<StockHistory>> assetStockHistory) {

		PortfolioPerformance portfolioPerformance = portfolioPerformanceMap.get(portfolio.getPortfolioId());
		if (null == portfolioPerformance) {
			portfolioPerformance = new PortfolioPerformance();
			portfolioPerformanceMap.put(portfolio.getPortfolioId(), portfolioPerformance);
		}

		// Assets
		Iterator<Map.Entry<AssetClass, List<StockHistory>>> assetStockIterator = assetStockHistory.entrySet()
				.iterator();
		while (assetStockIterator.hasNext()) {
			Map.Entry<AssetClass, List<StockHistory>> entry = assetStockIterator.next();
			AssetClass assetClass = entry.getKey();
			List<StockHistory> stockHistoryList = entry.getValue();

			// if there are no history data for the time interval it makes no sense to do
			// anything
			// so jump to the next asset
			if (null == stockHistoryList || stockHistoryList.isEmpty()) {
				continue;
			}

			AssetPerformance assetPerformance = assetPerformanceMap.get(assetClass.getAssetClassId());
			if (null == assetPerformance) {
				assetPerformance = new AssetPerformance();
				assetPerformance.setAssetClassId(assetClass.getAssetClassId());
				assetPerformance.setAssetClassName(assetClass.getAssetClassName());
				assetPerformanceMap.put(assetClass.getAssetClassId(), assetPerformance);
			}

			Iterator<StockHistory> historyIterator = stockHistoryList.iterator();
			while (historyIterator.hasNext()) {
				StockHistory stockHistory = historyIterator.next();

				// process new stock entries only
				if (processedStockDetailEntryMap.containsKey(stockHistory.getStockHistoryId())) {
					continue;
				}

				// ! Performance
				if (assetPerformance.getDailyDetails().containsKey(stockHistory.getHistoryDate())) {
					continue;
				}

				// History Entry wasn't processed until now
				assetPerformance = prepareAssetPerformanceByDate(assetPerformance, stockHistory);
//				assetPerformance = prepareAssetPerformanceByMonth(assetPerformance, stockHistory);
//				assetPerformance = prepareAssetPerformanceByYear(assetPerformance, stockHistory);

				if (null == assetPerformance.getStartDate()
						|| stockHistory.getHistoryDate().isBefore(assetPerformance.getStartDate())) {
					assetPerformance.setStartDate(stockHistory.getHistoryDate());
					assetPerformance.setOpen(stockHistory.getOpen());
				}
				if (null == assetPerformance.getEndDate()
						|| stockHistory.getHistoryDate().isAfter(assetPerformance.getEndDate())) {
					assetPerformance.setEndDate(stockHistory.getHistoryDate());
					assetPerformance.setClose(stockHistory.getClose());
				}
			}
			preparePortfolioPerformanceByDate(portfolioPerformance, assetPerformance);
//			preparePortfolioPerformanceByMonth(portfolioPerformance, assetPerformance);
//			preparePortfolioPerformanceByYear(portfolioPerformance, assetPerformance);
		}

		// mark history entries as processed
		Iterator<AssetPerformance> assetPerformanceIterator = assetPerformanceMap.values().iterator();
		while (assetPerformanceIterator.hasNext()) {
			AssetPerformance assetPerformance = assetPerformanceIterator.next();

			if (null == assetPerformance.getDailyDetails() || assetPerformance.getDailyDetails().isEmpty()) {
				continue;
			}

			Iterator<StockQuotationDetail> it = assetPerformance.getDailyDetails().values().iterator();
			while (it.hasNext()) {
				StockQuotationDetail stockQuotationDetail = it.next();
				processedStockDetailEntryMap.put(stockQuotationDetail.getStockHistoryId(),
						stockQuotationDetail.getStockHistoryId());
			}

		}
//			preparePortfolioPerformanceByMonth(portfolioPerformance, assetPerformance);
//			preparePortfolioPerformanceByYear(portfolioPerformance, assetPerformance);
//			preparePortfolioPerformanceByDateAndMarkHistoryEntryAsProcessed(portfolioPerformance, assetPerformance);
//		}

	}

	private PortfolioPerformance preparePortfolioPerformanceByYear(PortfolioPerformance portfolioPerformance,
			AssetPerformance assetPerformance) {
		if (null == portfolioPerformance.getStartDate()
				|| portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
			portfolioPerformance.setStartDate(assetPerformance.getStartDate());
		}

		if (null == portfolioPerformance.getEndDate()
				|| portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
			portfolioPerformance.setEndDate(assetPerformance.getEndDate());
		}

		Iterator<StockQuotationDetail> it = assetPerformance.getYearDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();

			if (processedStockDetailEntryMap.containsKey(assetDetail.getStockHistoryId())) {
				continue;
			}

			StockQuotationDetail portfolioDetail = portfolioPerformance.getYearDetails().get(assetDetail.getYear());
			if (null == portfolioDetail) {
				portfolioDetail = new StockQuotationDetail();
				portfolioDetail.setStartDate(assetDetail.getStartDate());
				portfolioDetail.setEndDate(assetDetail.getEndDate());
				portfolioDetail.setYear(assetDetail.getYear());
				portfolioPerformance.getYearDetails().put(portfolioDetail.getYear(), portfolioDetail);
			}
			portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
			portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());
		}

		if (portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
			portfolioPerformance.setStartDate(assetPerformance.getStartDate());
		}
		if (portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
			portfolioPerformance.setEndDate(assetPerformance.getEndDate());
		}
		return portfolioPerformance;
	}

	private PortfolioPerformance preparePortfolioPerformanceByMonth(PortfolioPerformance portfolioPerformance,
			AssetPerformance assetPerformance) {
		if (null == portfolioPerformance.getStartDate()
				|| portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
			portfolioPerformance.setStartDate(assetPerformance.getStartDate());
		}

		if (null == portfolioPerformance.getEndDate()
				|| portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
			portfolioPerformance.setEndDate(assetPerformance.getEndDate());
		}

		Iterator<StockQuotationDetail> it = assetPerformance.getMonthlyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();

			if (processedStockDetailEntryMap.containsKey(assetDetail.getStockHistoryId())) {
				continue;
			}

			StockQuotationDetail portfolioDetail = portfolioPerformance.getMonthlyDetails()
					.get(assetDetail.getMonthOfYear());
			if (null == portfolioDetail) {
				portfolioDetail = new StockQuotationDetail();
				portfolioDetail.setStartDate(assetDetail.getStartDate());
				portfolioDetail.setEndDate(assetDetail.getEndDate());
				portfolioDetail.setMonthOfYear(assetDetail.getMonthOfYear());
				portfolioPerformance.getMonthlyDetails().put(portfolioDetail.getMonthOfYear(), portfolioDetail);
			}
			portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
			portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());
		}
		return portfolioPerformance;
	}

	private PortfolioPerformance preparePortfolioPerformanceByDate(PortfolioPerformance portfolioPerformance,
			AssetPerformance assetPerformance) {
		if (null == portfolioPerformance.getStartDate()
				|| portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
			portfolioPerformance.setStartDate(assetPerformance.getStartDate());
		}

		if (null == portfolioPerformance.getEndDate()
				|| portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
			portfolioPerformance.setEndDate(assetPerformance.getEndDate());
		}

		Iterator<StockQuotationDetail> it = assetPerformance.getDailyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();

			if (processedStockDetailEntryMap.containsKey(assetDetail.getStockHistoryId())) {
				continue;
			}

			StockQuotationDetail portfolioDetail = portfolioPerformance.getDailyDetails()
					.get(assetDetail.getStartDate());
			if (null == portfolioDetail) {
				portfolioDetail = new StockQuotationDetail();
				portfolioDetail.setStartDate(assetDetail.getStartDate());
				portfolioDetail.setEndDate(assetDetail.getEndDate());
				portfolioDetail.setDayOfWeek(assetDetail.getDayOfWeek());
				portfolioDetail.setMonthOfYear(assetDetail.getMonthOfYear());
				portfolioDetail.setYear(assetDetail.getYear());
				portfolioPerformance.getDailyDetails().put(portfolioDetail.getStartDate(), portfolioDetail);
			}
			portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
			portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());

		}
		return portfolioPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByDate(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

		DayOfWeek dayOfWeek = stockHistory.getHistoryDate().getDayOfWeek();
		Month month = stockHistory.getHistoryDate().getMonth();
		int year = stockHistory.getHistoryDate().getYear();

		StockQuotationDetail assetPerformanceDetail = new StockQuotationDetail();
		assetPerformanceDetail.setStockHistoryId(stockHistory.getStockHistoryId());
		assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setOpen(stockHistory.getOpen());
		assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setClose(stockHistory.getClose());
		assetPerformanceDetail.setDayOfWeek(dayOfWeek);
		assetPerformanceDetail.setMonthOfYear(month);
		assetPerformanceDetail.setYear(year);
		assetPerformance.getDailyDetails().put(assetPerformanceDetail.getStartDate(), assetPerformanceDetail);

		return assetPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByMonth(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

		Month month = stockHistory.getHistoryDate().getMonth();

		StockQuotationDetail assetPerformanceDetail = assetPerformance.getMonthlyDetails().get(month);
		if (null == assetPerformanceDetail) {
			assetPerformanceDetail = new StockQuotationDetail();
			assetPerformanceDetail.setMonthOfYear(month);
			assetPerformance.getMonthlyDetails().put(month, assetPerformanceDetail);
		}
		if (null == assetPerformanceDetail.getStartDate()
				|| assetPerformanceDetail.getStartDate().isAfter(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setOpen(stockHistory.getOpen());
		}
		if (null == assetPerformanceDetail.getEndDate()
				|| assetPerformanceDetail.getEndDate().isBefore(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setClose(stockHistory.getClose());
		}

		return assetPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByYear(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

		int year = stockHistory.getHistoryDate().getYear();

		StockQuotationDetail assetPerformanceDetail = assetPerformance.getYearDetails().get(year);
		if (null == assetPerformanceDetail) {
			assetPerformanceDetail = new StockQuotationDetail();
			assetPerformance.getYearDetails().put(year, assetPerformanceDetail);
			assetPerformanceDetail.setYear(year);
		}
		if (null == assetPerformanceDetail.getStartDate()
				|| assetPerformanceDetail.getStartDate().isAfter(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setOpen(stockHistory.getOpen());
		}
		if (null == assetPerformanceDetail.getEndDate()
				|| assetPerformanceDetail.getEndDate().isBefore(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setClose(stockHistory.getClose());
		}

		return assetPerformance;
	}

}
