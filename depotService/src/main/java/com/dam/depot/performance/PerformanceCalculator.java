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
//		Float openRateDayBefore = 0F;
		Float closeRateDayBefore = 0F;

		// VALUES FROM PORTFOLIO
		// day by day
		// from first invest date until today
		while (calculationDate.isBefore(endDate) || calculationDate.isEqual(endDate)) {
			DepotPerformanceDetail dailyDetail = new DepotPerformanceDetail();
			dailyDetail.setStartDate(calculationDate);
			dailyDetail.setEndDate(calculationDate);
//			dailyDetail.setAmountAtBegin(accountAmount);

			if (null != dailyStockDetails.get(calculationDate)) {
				// available data for this day
				dailyDetail.setPerformancePercent(dailyStockDetails.get(calculationDate).getPerformancePercent());
				dailyDetail.setOpenRate(dailyStockDetails.get(calculationDate).getOpenWeighted());
				dailyDetail.setCloseRate(dailyStockDetails.get(calculationDate).getCloseWeighted());
				closeRateDayBefore = dailyStockDetails.get(calculationDate).getCloseWeighted();
			} else {
				// Free day, no change
				dailyDetail.setMarketOpen(false);
				dailyDetail.setPerformancePercent(performanceDayBefore);
				dailyDetail.setOpenRate(closeRateDayBefore);
				dailyDetail.setCloseRate(closeRateDayBefore);
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
