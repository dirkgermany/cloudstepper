package com.dam.portfolio;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.StockHistory;
import com.dam.portfolio.types.PerformanceInterval;

public class PortfolioPerformance {

	private Map<Date, PerformanceDetail> portfolioPerformanceDetails = new HashMap<>();
	private Map<Long, AssetPerformance> assetPerformanceDetails = new HashMap<>();
	
	private Float portfolioOpen = 0F;
	private Float portfolioClose = 0F;
	
	public Float getPortfolioOpen () {
		return portfolioOpen;
	}
	
	public Float getPortfolioClose () {
		return portfolioClose;
	}
	
	public Float calculatePortfolioPerformance () {
		return 1F+1;
	}
	
	/**
	 * Delivers Performance calculated by open and close of the interval (startDate,
	 * endDate) Both StartDate and EndDate MUST be within the earlier
	 * generatedLists!
	 * 
	 * @param performanceInterval
	 * @return
	 */
	public Float getPortfolioPerformance(Date startDate, Date endDate) {
		if (!portfolioPerformanceDetails.containsKey(startDate) || !portfolioPerformanceDetails.containsKey(endDate)) {
			return null;
		}
		
		// Berechnen auf Basis open startDate und close endDate
		return null;
	}

	/**
	 * Delivers a summary over all asset classes
	 */
	public int getPerformanceSummaryByMonth() {
		
		für alle assets getAssetPerformanceByMonth aufrufen
		
		return 0;
	}

	/**
	 * Delivers monthly informations for a dedicated asset class
	 */
	public List<PerformanceDetail> getAssetPerformanceByMonth(AssetClass assetClass) {
		Map<Month, PerformanceDetail> performanceMonthlylList = new HashMap<>();
		List<PerformanceDetail> details = assetPerformanceDetails.get(assetClass.getAssetClassId()).getPerformanceDetails();
		
		Date portfolioCalcStartDate = null;
		Date portfolioCalcEndDate = null;
		
		Iterator<PerformanceDetail> it = details.iterator();
		while (it.hasNext()) {			
			PerformanceDetail dailyListDetail = it.next();
			
			die nächsten Zeilen gehören in getPerformanceSummayByMonth
			hier werden nur Details für einzelne Assets gesammelt
			
			if (null == portfolioCalcStartDate || portfolioCalcStartDate.after(dailyListDetail.getDate())) {
				portfolioOpen = dailyListDetail.getOpen();				
				portfolioCalcStartDate = dailyListDetail.getDate();
			}
			
			if (null == portfolioCalcEndDate || portfolioCalcEndDate.before(dailyListDetail.getDate())) {
				portfolioClose = dailyListDetail.getClose();				
				portfolioCalcEndDate = dailyListDetail.getDate();
			}
			
			
			Month month = dailyListDetail.getMonthOfYear();
			PerformanceDetail monthlyDetail = performanceMonthlylList.get(month);
			
			if (null == monthlyDetail) {
				monthlyDetail = new PerformanceDetail();
				monthlyDetail.setDate(dailyListDetail.getDate());
				monthlyDetail.setDayOfWeek(dailyListDetail.getDayOfWeek());
				monthlyDetail.setMonthOfYear(dailyListDetail.getMonthOfYear());
				monthlyDetail.setYear(dailyListDetail.getYear());
				performanceMonthlylList.put(month, monthlyDetail);
				monthlyDetail.setOpen(dailyListDetail.getOpen());
				monthlyDetail.setClose(dailyListDetail.getClose());
			}
			
			if (dailyListDetail.getDate().before(monthlyDetail.getDate())) {
				monthlyDetail.setOpen(dailyListDetail.getOpen());
			}
			if (dailyListDetail.getDate().after(monthlyDetail.getDate())) {
				monthlyDetail.setClose(dailyListDetail.getClose());
			}
		}
		return new ArrayList<PerformanceDetail> (performanceMonthlylList.values());
	}

	public void generatePerformanceLists(PerformanceInterval performanceInterval,
			Map<AssetClass, List<StockHistory>> assetStockHistory) {

		// über alle Einträge der MAP iterieren und
		// a) für jeden Tag die Werte für das Portfolio addieren
		// b) für jeden Tag einen Eintrag für die Assets vorhalten

		// Wochentag / Monat / Jahr aus Datum ermitteln

		Iterator<Map.Entry<AssetClass, List<StockHistory>>> it = assetStockHistory.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<AssetClass, List<StockHistory>> entry = it.next();
			AssetClass asset = entry.getKey();
			AssetPerformance assetPerformance = prepareAssetPerformance(asset);

			List<StockHistory> stockHistoryList = entry.getValue();
			Iterator<StockHistory> historyIterator = stockHistoryList.iterator();
			while (historyIterator.hasNext()) {
				StockHistory history = historyIterator.next();
				PerformanceDetail assetPerformanceDetail = preparePerformanceDetailForAsset(history);
				assetPerformance.getPerformanceDetails().add(assetPerformanceDetail);

				preparePerformanceDetailForPortfolio(history, assetPerformanceDetail);
			}
		}
	}

	private PerformanceDetail preparePerformanceDetailForPortfolio(StockHistory stockHistory,
			PerformanceDetail performanceDetailOfAsset) {
		PerformanceDetail performanceDetail = portfolioPerformanceDetails.get(stockHistory.getHistoryDate());
		if (null == performanceDetail) {
			performanceDetail = new PerformanceDetail();
			portfolioPerformanceDetails.put(stockHistory.getHistoryDate(), performanceDetail);
		} else {
			performanceDetail.setOpen(performanceDetail.getOpen() + stockHistory.getOpen());
			performanceDetail.setClose(performanceDetail.getClose() + stockHistory.getClose());
		}
		performanceDetail.setDate(stockHistory.getHistoryDate());
		performanceDetail.setDayOfWeek(performanceDetailOfAsset.getDayOfWeek());
		performanceDetail.setMonthOfYear(performanceDetailOfAsset.getMonthOfYear());

		return performanceDetail;
	}

	private AssetPerformance prepareAssetPerformance(AssetClass asset) {
		AssetPerformance assetPerformance = assetPerformanceDetails.get(asset.getAssetClassId());
		if (null == assetPerformance) {
			assetPerformance = new AssetPerformance();
			assetPerformance.setAssetClass(asset);
			assetPerformanceDetails.put(asset.getAssetClassId(), assetPerformance);
		}
		return assetPerformance;
	}

	private PerformanceDetail preparePerformanceDetailForAsset(StockHistory stockHistory) {
		PerformanceDetail performanceDetail = new PerformanceDetail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(stockHistory.getHistoryDate());
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		int year = cal.get(Calendar.YEAR);
		performanceDetail.setMonthOfYear(Month.of(month - 1));
		performanceDetail.setDayOfWeek(DayOfWeek.of(day - 1));
		performanceDetail.setYear(Year.of(year));
		performanceDetail.setDate(stockHistory.getHistoryDate());
		performanceDetail.setOpen(stockHistory.getOpen());
		performanceDetail.setClose(stockHistory.getClose());

		return performanceDetail;
	}

	public class PerformanceDetail {
		private Date date;
		private DayOfWeek dayOfWeek;
		private Month monthOfYear;
		private Year year;
		private Float open = 0F;
		private Float close = 0F;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public DayOfWeek getDayOfWeek() {
			return dayOfWeek;
		}

		public void setDayOfWeek(DayOfWeek dayOfWeek) {
			this.dayOfWeek = dayOfWeek;
		}

		public Month getMonthOfYear() {
			return monthOfYear;
		}

		public void setMonthOfYear(Month monthOfYear) {
			this.monthOfYear = monthOfYear;
		}

		public Float getOpen() {
			return open;
		}

		public void setOpen(Float open) {
			this.open = open;
		}

		public Float getClose() {
			return close;
		}

		public void setClose(Float close) {
			this.close = close;
		}

		public Year getYear() {
			return year;
		}

		public void setYear(Year year) {
			this.year = year;
		}
	}

	public class AssetPerformance {
		private AssetClass assetClass;
		private List<PerformanceDetail> performanceDetails = new ArrayList<>();

		public AssetClass getAssetClass() {
			return assetClass;
		}

		public void setAssetClass(AssetClass assetClass) {
			this.assetClass = assetClass;
		}

		public List<PerformanceDetail> getPerformanceDetails() {
			return performanceDetails;
		}

		public void setPerformanceDetails(List<PerformanceDetail> performanceDetails) {
			this.performanceDetails = performanceDetails;
		}
	}

}
