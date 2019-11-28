package com.dam.depot.performance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dam.depot.model.entity.DepotTransaction;

public class PerformanceCalculator {

	public List<DepotPerformanceDetail> calculateDepotPerformance(LocalDate startDate, LocalDate endDate,
			List<DepotTransaction> depotTransactionList, Map<LocalDate, StockQuotationDetail> dailyStockDetails) {
		LocalDate calculationDate = startDate;
		int depotIndex = 0;
//		Float accountAmount = 0F;
		List<DepotPerformanceDetail> dailyDetails = new ArrayList<>();
		Float invest = 0F;
		Float investAtAll = 0F;
		Float performanceDayBefore = 0F;

		// VALUES FROM PORTFOLIO
		// day by day
		// from first invest date until today
		while (calculationDate.isBefore(endDate) || calculationDate.isEqual(endDate)) {
			DepotPerformanceDetail dailyDetail = new DepotPerformanceDetail();
			dailyDetail.setStartDate(calculationDate);
			dailyDetail.setEndDate(calculationDate);
//			dailyDetail.setAmountAtBegin(accountAmount);

			// Free day, no change
			if (null != dailyStockDetails.get(calculationDate)) {
				dailyDetail.setPerformancePercent(dailyStockDetails.get(calculationDate).getPerformancePercent());
				dailyDetail.setOpenRate(dailyStockDetails.get(calculationDate).getOpen());
				dailyDetail.setCloseRate(dailyStockDetails.get(calculationDate).getClose());
			} else {
				dailyDetail.setMarketOpen(false);
				dailyDetail.setPerformancePercent(performanceDayBefore);
			}

			// also valid for the first time because the first entry of depotTransactions
			// must have the
			// same date as the daily stock list
			if (depotIndex < depotTransactionList.size()) {
				if (depotTransactionList.get(depotIndex).getActionDate().toLocalDate().isEqual(calculationDate)) {
					// consider payment
					Float returnOfInvest;
					// first data
					if (calculationDate.isEqual(startDate)) {
						returnOfInvest = 0F;
					} else {
						// in the morning use the performance of the day before for calculation
						returnOfInvest = investAtAll * (1 + (performanceDayBefore / 100));
					}

					// in the evening
//					dailyDetail.setInvest(depotTransactionList.get(depotIndex).getAmount());
					investAtAll = depotTransactionList.get(depotIndex).getAmount() + returnOfInvest;
					invest+=depotTransactionList.get(depotIndex).getAmount();
					depotIndex++;
				}
			}
			
			if (null != dailyStockDetails.get(calculationDate)) {
				performanceDayBefore = dailyStockDetails.get(calculationDate).getPerformancePercent();
			}
			
			dailyDetail.setInvest(invest);
			dailyDetail.setInvestAtAll(investAtAll);
			dailyDetails.add(dailyDetail);
			calculationDate = calculationDate.plusDays(1);
		}
		return dailyDetails;
	}

}
