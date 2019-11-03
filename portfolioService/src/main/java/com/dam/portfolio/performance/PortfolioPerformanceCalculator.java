package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

//	private Map<Long, PortfolioPerformance> portfolioPerformanceMap;
	private PortfolioPerformance portfolioPerformance;
	private Map<Long, AssetPerformance> assetPerformanceMap;
	private final Map<LocalDate, StockQuotationDetail> calculationMap = new TreeMap<>();
//	private static Map<Long, Long> processedStockDetailEntryMap = new HashMap<>();

	/**
	 * Summary over all asset details.
	 * 
	 * @param portfolioId
	 * @return
	 */
	public PortfolioPerformance getPortfolioPerformance() {
		return this.portfolioPerformance;
	}

	public Map<Long, AssetPerformance> getAssetPerformanceMap() {
		return assetPerformanceMap;
	}

	public AssetPerformance getAssetPerformance(Long assetClassId) {
		return assetPerformanceMap.get(assetClassId);
	}

	public List<AssetPerformance> getAssetPerformanceList() {
		return new ArrayList<AssetPerformance>(assetPerformanceMap.values());
	}

	/*
	 * Must be called at first. Initializes the performance values. The performance
	 * values are stored static.
	 */
	public PortfolioPerformance generatePerformanceLists(Portfolio portfolio,
			Map<AssetClass, List<StockHistory>> assetStockHistory, LocalDate startDate, LocalDate endDate) {

		portfolioPerformance = new PortfolioPerformance();
		assetPerformanceMap = new HashMap<>();

		portfolioPerformance.setPortfolioId(portfolio.getPortfolioId());
		portfolioPerformance.setCallMoneyPct(portfolio.getCallMoneyPct());
		portfolioPerformance.setEtfPct(portfolio.getEtfPct());
		portfolioPerformance.setGoldPct(portfolio.getGoldPct());
		portfolioPerformance.setLoanPct(portfolio.getLoanPct());

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
				assetPerformance.setWeighting(assetClass.getPortfolioWeighting());
				assetPerformance.setAssetClassType(assetClass.getAssetClassType());
				assetPerformance.setSymbol(assetClass.getSymbol());
				assetPerformanceMap.put(assetClass.getAssetClassId(), assetPerformance);
			}

			Iterator<StockHistory> historyIterator = stockHistoryList.iterator();
			while (historyIterator.hasNext()) {
				StockHistory stockHistory = historyIterator.next();

				// accept only stockHistory entries which are within the date range
				if (stockHistory.getHistoryDate().isBefore(startDate)
						|| stockHistory.getHistoryDate().isAfter(endDate)) {
					continue;
				}

				// History Entry wasn't processed until now
				assetPerformance = prepareAssetPerformanceByDate(assetPerformance, stockHistory);
			}
			preparePortfolioPerformanceByDate(portfolioPerformance, assetPerformance);
		}

		return portfolioPerformance;
	}
	
	private Float calculateWeightedValueOfAsset(Float value, Float weighting) {
		return value*weighting/100;
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
		
		if (portfolioPerformance.getStartDate().isEqual(assetPerformance.getStartDate())) {
//			portfolioPerformance.addToOpen(calculateWeightedValueOfAsset(assetPerformance.getOpen(), assetPerformance.getWeighting()));
			portfolioPerformance.addToOpen(assetPerformance.getOpen());
			
			ClassTypeValues values = portfolioPerformance.getClassTypeValuesMap().get(assetPerformance.getAssetClassType());
			if (null == values) {
				values = new ClassTypeValues();
				portfolioPerformance.getClassTypeValuesMap().put(assetPerformance.getAssetClassType(), values);
			}
			values.addToOpen(assetPerformance.getOpen());
			AssetClassValues assetValues = values.getAssetClassValues().get(assetPerformance.getSymbol());
			if (null == assetValues) {
				assetValues = new AssetClassValues();
				values.getAssetClassValues().put(assetPerformance.getSymbol(), assetValues);
			}
			assetValues.setOpen(assetPerformance.getOpen());
		}
		if (portfolioPerformance.getEndDate().isEqual(assetPerformance.getEndDate())) {
//			portfolioPerformance.addToClose(calculateWeightedValueOfAsset(assetPerformance.getClose(), assetPerformance.getWeighting()));
			portfolioPerformance.addToClose(assetPerformance.getClose());

			ClassTypeValues values = portfolioPerformance.getClassTypeValuesMap().get(assetPerformance.getAssetClassType());
			if (null == values) {
				values = new ClassTypeValues();
				portfolioPerformance.getClassTypeValuesMap().put(assetPerformance.getAssetClassType(), values);
			}
			values.addToClose(assetPerformance.getClose());
			AssetClassValues assetValues = values.getAssetClassValues().get(assetPerformance.getSymbol());
			if (null == assetValues) {
				assetValues = new AssetClassValues();
				values.getAssetClassValues().put(assetPerformance.getSymbol(), assetValues);
			}
			assetValues.setClose(assetPerformance.getClose());
		}

		Iterator<StockQuotationDetail> it = assetPerformance.getDailyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();
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

			portfolioDetail.addToOpen(assetDetail.getOpen());
			portfolioDetail.addToClose(assetDetail.getClose());
		}
		return portfolioPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByDate(AssetPerformance assetPerformance,
			StockHistory stockHistory) {

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

//	private AssetPerformance prepareAssetPerformanceByMonth(AssetPerformance assetPerformance,
//			StockHistory stockHistory) {
//
//		Month month = stockHistory.getHistoryDate().getMonth();
//
//		StockQuotationDetail assetPerformanceDetail = assetPerformance.getMonthlyDetails().get(month);
//		if (null == assetPerformanceDetail) {
//			assetPerformanceDetail = new StockQuotationDetail();
//			assetPerformanceDetail.setMonthOfYear(month);
//			assetPerformance.getMonthlyDetails().put(month, assetPerformanceDetail);
//		}
//		if (null == assetPerformanceDetail.getStartDate()
//				|| assetPerformanceDetail.getStartDate().isAfter(stockHistory.getHistoryDate())) {
//			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
//			assetPerformanceDetail.setOpen(stockHistory.getOpen());
//		}
//		if (null == assetPerformanceDetail.getEndDate()
//				|| assetPerformanceDetail.getEndDate().isBefore(stockHistory.getHistoryDate())) {
//			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
//			assetPerformanceDetail.setClose(stockHistory.getClose());
//		}
//
//		return assetPerformance;
//	}
//
//	private AssetPerformance prepareAssetPerformanceByYear(AssetPerformance assetPerformance,
//			StockHistory stockHistory) {
//
//		int year = stockHistory.getHistoryDate().getYear();
//
//		StockQuotationDetail assetPerformanceDetail = assetPerformance.getYearDetails().get(year);
//		if (null == assetPerformanceDetail) {
//			assetPerformanceDetail = new StockQuotationDetail();
//			assetPerformance.getYearDetails().put(year, assetPerformanceDetail);
//			assetPerformanceDetail.setYear(year);
//		}
//		if (null == assetPerformanceDetail.getStartDate()
//				|| assetPerformanceDetail.getStartDate().isAfter(stockHistory.getHistoryDate())) {
//			assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
//			assetPerformanceDetail.setOpen(stockHistory.getOpen());
//		}
//		if (null == assetPerformanceDetail.getEndDate()
//				|| assetPerformanceDetail.getEndDate().isBefore(stockHistory.getHistoryDate())) {
//			assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
//			assetPerformanceDetail.setClose(stockHistory.getClose());
//		}
//
//		return assetPerformance;
//	}

//	private PortfolioPerformance preparePortfolioPerformanceByYear(PortfolioPerformance portfolioPerformance,
//	AssetPerformance assetPerformance) {
//if (null == portfolioPerformance.getStartDate()
//		|| portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
//	portfolioPerformance.setStartDate(assetPerformance.getStartDate());
//}
//
//if (null == portfolioPerformance.getEndDate()
//		|| portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
//	portfolioPerformance.setEndDate(assetPerformance.getEndDate());
//}
//
//Iterator<StockQuotationDetail> it = assetPerformance.getYearDetails().values().iterator();
//while (it.hasNext()) {
//	StockQuotationDetail assetDetail = it.next();
//
//	if (processedStockDetailEntryMap.containsKey(assetDetail.getStockHistoryId())) {
//		continue;
//	}
//
//	StockQuotationDetail portfolioDetail = portfolioPerformance.getYearDetails().get(assetDetail.getYear());
//	if (null == portfolioDetail) {
//		portfolioDetail = new StockQuotationDetail();
//		portfolioDetail.setStartDate(assetDetail.getStartDate());
//		portfolioDetail.setEndDate(assetDetail.getEndDate());
//		portfolioDetail.setYear(assetDetail.getYear());
//		portfolioPerformance.getYearDetails().put(portfolioDetail.getYear(), portfolioDetail);
//	}
//	portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
//	portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());
//}
//
//if (portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
//	portfolioPerformance.setStartDate(assetPerformance.getStartDate());
//}
//if (portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
//	portfolioPerformance.setEndDate(assetPerformance.getEndDate());
//}
//return portfolioPerformance;
//}

//private PortfolioPerformance preparePortfolioPerformanceByMonth(PortfolioPerformance portfolioPerformance,
//	AssetPerformance assetPerformance) {
//if (null == portfolioPerformance.getStartDate()
//		|| portfolioPerformance.getStartDate().isAfter(assetPerformance.getStartDate())) {
//	portfolioPerformance.setStartDate(assetPerformance.getStartDate());
//}
//
//if (null == portfolioPerformance.getEndDate()
//		|| portfolioPerformance.getEndDate().isBefore(assetPerformance.getEndDate())) {
//	portfolioPerformance.setEndDate(assetPerformance.getEndDate());
//}
//
//Iterator<StockQuotationDetail> it = assetPerformance.getMonthlyDetails().values().iterator();
//while (it.hasNext()) {
//	StockQuotationDetail assetDetail = it.next();
//
//	if (processedStockDetailEntryMap.containsKey(assetDetail.getStockHistoryId())) {
//		continue;
//	}
//
//	StockQuotationDetail portfolioDetail = portfolioPerformance.getMonthlyDetails()
//			.get(assetDetail.getMonthOfYear());
//	if (null == portfolioDetail) {
//		portfolioDetail = new StockQuotationDetail();
//		portfolioDetail.setStartDate(assetDetail.getStartDate());
//		portfolioDetail.setEndDate(assetDetail.getEndDate());
//		portfolioDetail.setMonthOfYear(assetDetail.getMonthOfYear());
//		portfolioPerformance.getMonthlyDetails().put(portfolioDetail.getMonthOfYear(), portfolioDetail);
//	}
//	portfolioDetail.setOpen(portfolioDetail.getOpen() + assetDetail.getOpen());
//	portfolioDetail.setClose(portfolioDetail.getClose() + assetDetail.getClose());
//}
//return portfolioPerformance;
//}
}
