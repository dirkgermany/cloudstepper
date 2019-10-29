package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.model.entity.StockHistory;

@Component
public class PortfolioPerformanceCalculator {

	private static Map<Long, PortfolioPerformance> portfolioPerformanceMap = new HashMap<>();
	private static Map<Long, AssetPerformance> assetPerformanceMap = new HashMap<>();

	/**
	 * Summary over all asset details.
	 * 
	 * @param portfolioId
	 * @return
	 */
	public PortfolioPerformance getPortfolioPerformance(Long portfolioId) {
		return portfolioPerformanceMap.get(portfolioId);
	}

	public Map<Long, AssetPerformance> getAssetPerformanceMap() {
		return assetPerformanceMap;
	}
	
	public List<AssetPerformance> getAssetPerformanceList() {
		return new ArrayList<>(assetPerformanceMap.values());
	}

	/*
	 * Must be called at first. Initializes the performance values.
	 * The performance values are stored static.
	 */
	public void generatePerformanceLists(Portfolio portfolio, Map<AssetClass, List<StockHistory>> assetStockHistory) {
		
		PortfolioPerformance portfolioPerformance = portfolioPerformanceMap.get(portfolio.getPortfolioId());
		if (null == portfolioPerformance) {
			portfolioPerformance = new PortfolioPerformance();
			portfolioPerformanceMap.put(portfolio.getPortfolioId(), portfolioPerformance);
		}

		// Assets
		Iterator<Map.Entry<AssetClass, List<StockHistory>>> assetStockIterator = assetStockHistory.entrySet().iterator();
		while (assetStockIterator.hasNext()) {
			Map.Entry<AssetClass, List<StockHistory>> entry = assetStockIterator.next();
			AssetClass assetClass = entry.getKey();
			AssetPerformance assetPerformance = assetPerformanceMap.get(assetClass.getAssetClassId());
			if (null == assetPerformance) {
				assetPerformance = new AssetPerformance();
				assetPerformance.setAssetClassId(assetClass.getAssetClassId());
				assetPerformance.setAssetClassName(assetClass.getAssetClassName());
				assetPerformanceMap.put(assetClass.getAssetClassId(), assetPerformance);
			}

			List<StockHistory> stockHistoryList = entry.getValue();
			Iterator<StockHistory> historyIterator = stockHistoryList.iterator();
			while (historyIterator.hasNext()) {
				StockHistory stockHistory = historyIterator.next();
				
				// ! Performance
				if (assetPerformance.getDailyDetails().containsKey(stockHistory.getHistoryDate())) {
					continue;
				}
				
				// History Entry wasn't processed until now
				assetPerformance = prepareAssetPerformanceByDate(assetPerformance, stockHistory);
				assetPerformance = prepareAssetPerformanceByMonth(assetPerformance, stockHistory);
				assetPerformance = prepareAssetPerformanceByYear(assetPerformance, stockHistory);

				if (stockHistory.getHistoryDate().before(assetPerformance.getStartDate())) {
					assetPerformance.setOpen(stockHistory.getOpen());
				}
				if (stockHistory.getHistoryDate().after(assetPerformance.getEndDate())) {
					assetPerformance.setClose(stockHistory.getClose());
				}
				
				preparePortfolioPerformanceByDate(portfolioPerformance, assetPerformance);
			}
		}
		
		// Portfolio		
		Iterator <AssetPerformance> assetPerformanceIterator = assetPerformanceMap.values().iterator();		
		while (assetPerformanceIterator.hasNext()) {
			AssetPerformance assetPerformance = assetPerformanceIterator.next();
			
			// Performance
			// If startDate and endDate of portfolio are the same as of asset all days of the interval have been processed before
			if (0 == assetPerformance.getStartDate().compareTo(portfolioPerformance.getStartDate()) || 0 == assetPerformance.getEndDate().compareTo(portfolioPerformance.getEndDate())) {
				continue;
			}
			
			preparePortfolioPerformanceByMonth(portfolioPerformance, assetPerformance);
			preparePortfolioPerformanceByYear(portfolioPerformance, assetPerformance);
		}
		
	}
	
	private void preparePortfolioPerformanceByYear(PortfolioPerformance portfolioPerformance, AssetPerformance assetPerformance) {
		Iterator<StockQuotationDetail> it = assetPerformance.getYearDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();
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
		if (portfolioPerformance.getStartDate().after(assetPerformance.getStartDate())) {
			portfolioPerformance.setStartDate(assetPerformance.getStartDate());
		}
		if (portfolioPerformance.getEndDate().after(assetPerformance.getEndDate())) {
			portfolioPerformance.setEndDate(assetPerformance.getEndDate());
		}
	}

	private void preparePortfolioPerformanceByMonth(PortfolioPerformance portfolioPerformance, AssetPerformance assetPerformance) {
		Iterator<StockQuotationDetail> it = assetPerformance.getMonthlyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();
			StockQuotationDetail portfolioDetail = portfolioPerformance.getMonthlyDetails().get(assetDetail.getMonthOfYear());
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
	}
	
	private void preparePortfolioPerformanceByDate(PortfolioPerformance portfolioPerformance, AssetPerformance assetPerformance) {
		Iterator<StockQuotationDetail> it = assetPerformance.getDailyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();
			StockQuotationDetail portfolioDetail = portfolioPerformance.getDailyDetails().get(assetDetail.getStartDate());
			if (null == portfolioDetail) {
				portfolioDetail = new StockQuotationDetail();
				portfolioDetail.setStartDate(assetDetail.getStartDate());
				portfolioDetail.setEndDate(assetDetail.getEndDate());
				portfolioDetail.setDayOfWeek(assetDetail.getDayOfWeek());
				portfolioPerformance.getDailyDetails().put(portfolioDetail.getStartDate(), portfolioDetail);
			}
			portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
			portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());
		}
	}

	private AssetPerformance prepareAssetPerformanceByDate(AssetPerformance assetPerformance,
			StockHistory stockHistory) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(stockHistory.getHistoryDate());
		DayOfWeek day = DayOfWeek.of(cal.get(Calendar.DAY_OF_WEEK) - 1);

		StockQuotationDetail assetPerformanceDetail = new StockQuotationDetail();
		assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setOpen(stockHistory.getOpen());
		assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setClose(stockHistory.getClose());
		assetPerformanceDetail.setDayOfWeek(day);
		assetPerformance.getDailyDetails().put(assetPerformanceDetail.getStartDate(), assetPerformanceDetail);

		return assetPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByMonth(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(stockHistory.getHistoryDate());
		Month month = Month.of(cal.get(Calendar.MONTH) - 1);

		StockQuotationDetail assetPerformanceDetail = assetPerformance.getMonthlyDetails().get(month);
		if (null == assetPerformanceDetail) {
			assetPerformanceDetail = new StockQuotationDetail();
			assetPerformanceDetail.setMonthOfYear(month);
			assetPerformance.getMonthlyDetails().put(month, assetPerformanceDetail);
		}
		if (assetPerformanceDetail.getStartDate().after(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setOpen(stockHistory.getOpen());
		}
		if (assetPerformanceDetail.getEndDate().before(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setClose(stockHistory.getClose());
		}

		return assetPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByYear(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(stockHistory.getHistoryDate());
		Year year = Year.of(cal.get(Calendar.YEAR) - 1);

		StockQuotationDetail assetPerformanceDetail = assetPerformance.getYearDetails().get(year);
		if (null == assetPerformanceDetail) {
			assetPerformanceDetail = new StockQuotationDetail();
			assetPerformance.getYearDetails().put(year, assetPerformanceDetail);
		}
		if (assetPerformanceDetail.getStartDate().after(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setOpen(stockHistory.getOpen());
		}
		if (assetPerformanceDetail.getEndDate().before(stockHistory.getHistoryDate())) {
			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
			assetPerformanceDetail.setClose(stockHistory.getClose());
		}

		return assetPerformance;
	}

}
