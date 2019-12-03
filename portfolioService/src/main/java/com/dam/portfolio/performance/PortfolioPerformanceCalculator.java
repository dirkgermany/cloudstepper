package com.dam.portfolio.performance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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

	private PortfolioPerformance portfolioPerformance;
	private Map<Long, AssetPerformance> assetPerformanceMap;

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
			// AssetClass e.g. BOND or ETF or GOLD
			Map.Entry<AssetClass, List<StockHistory>> entry = assetStockIterator.next();
			AssetClass assetClass = entry.getKey();
			List<StockHistory> stockHistoryList = entry.getValue();

			// if there are no history data for the time interval it makes no sense to do
			// anything
			// so jump to the next asset
			if (null == stockHistoryList || stockHistoryList.isEmpty()) {
				continue;
			}

			// AssetPerformance object holds all daily values for one asset, e.g. IHI
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
				// StockHistory hold values for one asset for one day
				StockHistory stockHistory = historyIterator.next();

				// accept only stockHistory entries which are within the date range
				if (stockHistory.getHistoryDate().isBefore(startDate)
						|| stockHistory.getHistoryDate().isAfter(endDate)) {
					continue;
				}

				// History entry wasn't processed until now
				assetPerformance = prepareAssetPerformanceByDate(assetPerformance, stockHistory, portfolioPerformance);
			}
			
			preparePortfolioPerformanceByDate(portfolioPerformance, assetPerformance);
		}

		return portfolioPerformance;
	}

	private Float calculateWeightedValueOfAsset(Float value, Float assetWeighting, Float portfolioWeighting) {
		// result1 = portfolioWeighting of value
		// result2 = assetWeighting of result1
		// or:
		// weighting = assetWeighting of portfolioWeighting
		// result = weighting of value
		// sample: weighting = portfolioWeighting*assetWeighting/100
		// result = value*weighting/100
		// short: result = value*(portfolioWeighting*assetWeighting/100)/100
		// result = 1500*(40*50/100)/100
		// or: result = 1500*40*50/10000

		return value * portfolioWeighting * assetWeighting / 10000;
	}
	
	private Float lookupForAssetPercentage(AssetPerformance assetPerformance, PortfolioPerformance portfolioPerformance) {
		Float assetClassPercentage = lookupForAssetClassPercentage(assetPerformance, portfolioPerformance);
		return assetClassPercentage * assetPerformance.getWeighting()/10000;
	}

	private Float lookupForAssetClassPercentage(AssetPerformance assetPerformance, PortfolioPerformance portfolioPerformance) {
		Float percentage = 0F;
		switch (assetPerformance.getAssetClassType()) {
		case ETF:
			percentage = portfolioPerformance.getEtfPct();
			break;
			
		case BOND:
			percentage = portfolioPerformance.getLoanPct();
			break;

		case GOLD:
			percentage = portfolioPerformance.getGoldPct();
			break;

		default:
			break;
		}
		return percentage;
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
			Float assetWeightedValueOpen = calculateWeightedValueOfAsset(assetPerformance.getOpen(),assetPerformance.getWeighting(), lookupForAssetClassPercentage(assetPerformance, portfolioPerformance));
			portfolioPerformance.addToOpen(assetPerformance.getOpen());
			portfolioPerformance.addToOpenWeighted(assetWeightedValueOpen);

			// group by class types
			// add values per class type
			ClassTypeValues classTypeValues = portfolioPerformance.getClassTypeValuesMap().get(assetPerformance.getAssetClassType());
			if (null == classTypeValues) {
				classTypeValues = new ClassTypeValues();
				portfolioPerformance.getClassTypeValuesMap().put(assetPerformance.getAssetClassType(), classTypeValues);
			}
			classTypeValues.addToOpen(assetPerformance.getOpen());
			classTypeValues.addToOpenWeighted(assetWeightedValueOpen);
			AssetClassValues assetValues = classTypeValues.getAssetClassValues().get(assetPerformance.getSymbol());
			if (null == assetValues) {
				assetValues = new AssetClassValues();
				classTypeValues.getAssetClassValues().put(assetPerformance.getSymbol(), assetValues);
			}
			assetValues.setOpen(assetPerformance.getOpen());
			assetValues.setOpenWeighted(assetWeightedValueOpen);
			assetValues.setWeighting(assetPerformance.getWeighting());
		}
		
		if (portfolioPerformance.getEndDate().isEqual(assetPerformance.getEndDate())) {
			Float assetWeightedValueClose = calculateWeightedValueOfAsset(assetPerformance.getClose(), assetPerformance.getWeighting(), lookupForAssetClassPercentage(assetPerformance, portfolioPerformance));
			portfolioPerformance.addToClose(assetPerformance.getClose());
			portfolioPerformance.addToCloseWeighted(assetWeightedValueClose);

			ClassTypeValues classTypeValues = portfolioPerformance.getClassTypeValuesMap()
					.get(assetPerformance.getAssetClassType());
			if (null == classTypeValues) {
				classTypeValues = new ClassTypeValues();
				portfolioPerformance.getClassTypeValuesMap().put(assetPerformance.getAssetClassType(), classTypeValues);
			}
			classTypeValues.addToClose(assetPerformance.getClose());
			classTypeValues.addToCloseWeighted(assetWeightedValueClose);
			AssetClassValues assetValues = classTypeValues.getAssetClassValues().get(assetPerformance.getSymbol());
			if (null == assetValues) {
				assetValues = new AssetClassValues();
				classTypeValues.getAssetClassValues().put(assetPerformance.getSymbol(), assetValues);
			}
			assetValues.setClose(assetPerformance.getClose());
			assetValues.setCloseWeighted(assetWeightedValueClose);
			assetValues.setWeighting(assetPerformance.getWeighting());
		}

		Iterator<StockQuotationDetail> it = assetPerformance.getDailyDetails().values().iterator();
		while (it.hasNext()) {
			StockQuotationDetail assetDetail = it.next();
			StockQuotationDetail portfolioDetail = portfolioPerformance.getDailyDetails().get(assetDetail.getStartDate());
			if (null == portfolioDetail) {
				portfolioDetail = new StockQuotationDetail();
				portfolioDetail.setStartDate(assetDetail.getStartDate());
				portfolioDetail.setEndDate(assetDetail.getEndDate());
				portfolioDetail.setDayOfWeek(assetDetail.getDayOfWeek());
				portfolioDetail.setMonthOfYear(assetDetail.getMonthOfYear());
				portfolioDetail.setYear(assetDetail.getYear());
				portfolioPerformance.getDailyDetails().put(portfolioDetail.getStartDate(), portfolioDetail);
			}
			
			Float percentage = lookupForAssetClassPercentage(assetPerformance, portfolioPerformance);
			Float openWeighted = calculateWeightedValueOfAsset(assetDetail.getOpen(), assetPerformance.getWeighting(), percentage);
			Float closeWeighted = calculateWeightedValueOfAsset(assetDetail.getClose(), assetPerformance.getWeighting(), percentage);

			portfolioDetail.addToOpen(assetDetail.getOpen());
			portfolioDetail.addToClose(assetDetail.getClose());
			portfolioDetail.addToOpenWeighted(openWeighted);
			portfolioDetail.addToCloseWeighted(closeWeighted);
		}
		return portfolioPerformance;
	}

	private AssetPerformance prepareAssetPerformanceByDate(AssetPerformance assetPerformance,
			StockHistory stockHistory, PortfolioPerformance portfolioPerformance) {

		// find out first day of stock data for the asset 
		if (null == assetPerformance.getStartDate()
				|| stockHistory.getHistoryDate().isBefore(assetPerformance.getStartDate())) {
			assetPerformance.setStartDate(stockHistory.getHistoryDate());
			assetPerformance.setOpen(stockHistory.getOpen());
		}
		// find out last day of stock data for the asset 
		if (null == assetPerformance.getEndDate()
				|| stockHistory.getHistoryDate().isAfter(assetPerformance.getEndDate())) {
			assetPerformance.setEndDate(stockHistory.getHistoryDate());
			assetPerformance.setClose(stockHistory.getClose());
		}

		// informations
		DayOfWeek dayOfWeek = stockHistory.getHistoryDate().getDayOfWeek();
		Month month = stockHistory.getHistoryDate().getMonth();
		int year = stockHistory.getHistoryDate().getYear();
		
		// StockQuotationDetail holds stock informations PLUS portfolio weighted informations for one asset for one day
		StockQuotationDetail assetPerformanceDetail = new StockQuotationDetail();
		assetPerformanceDetail.setStockHistoryId(stockHistory.getStockHistoryId());
		assetPerformanceDetail.setStartDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setEndDate(stockHistory.getHistoryDate());
		assetPerformanceDetail.setOpen(stockHistory.getOpen());
		assetPerformanceDetail.setClose(stockHistory.getClose());
		
		Float assetPercentage = lookupForAssetPercentage(assetPerformance, portfolioPerformance);
		Float dailyPerformanceTotal = (stockHistory.getClose() / stockHistory.getOpen() -1) * 100;
		Float dailyPerformancePercentage = dailyPerformanceTotal * assetPercentage;
		Float performance = dailyPerformancePercentage / 100;
		Float performanceValue = assetPercentage + performance;
		assetPerformanceDetail.setPerformancePercentage(dailyPerformancePercentage);
		assetPerformanceDetail.setPerformanceTotal(dailyPerformanceTotal); 
		assetPerformanceDetail.setPerformance(performance);
		assetPerformanceDetail.setPerformanceValue(performanceValue);
		
		
		Float assetWeightedOpen = assetPerformanceDetail.getOpen() * assetPercentage;
		Float assetWeightedClose = assetPerformanceDetail.getClose() * assetPercentage;
		
		assetPerformanceDetail.setOpenWeighted(assetWeightedOpen);
		assetPerformanceDetail.setCloseWeighted(assetWeightedClose);
		assetPerformanceDetail.setDayOfWeek(dayOfWeek);
		assetPerformanceDetail.setMonthOfYear(month);
		assetPerformanceDetail.setYear(year);

		assetPerformance.getDailyDetails().put(assetPerformanceDetail.getStartDate(), assetPerformanceDetail);

		return assetPerformance;
	}
}
