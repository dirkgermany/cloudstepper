package com.dam.depot.performance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dam.depot.model.entity.DepotTransaction;

public class PerformanceCalculator {

	public static List<DepotPerformanceDetail> calculateDepotPerformance(LocalDate startDate, LocalDate endDate,
			List<DepotTransaction> depotTransactionList, Map<LocalDate, StockQuotationDetail> dailyStockDetails) {
		LocalDate calculationDate = startDate;
		int depotIndex = 0;
		Float accountAmount = 0F;
		List<DepotPerformanceDetail> dailyDetails = new ArrayList<>();
		Float fakePerformancePercent = 0F; // no data for weekend
		// day by day
		// from first invest date until today
		while (calculationDate.isBefore(endDate) || calculationDate.isEqual(endDate)) {
			DepotPerformanceDetail dailyDetail = new DepotPerformanceDetail();
			dailyDetail.setStartDate(calculationDate);
			dailyDetail.setEndDate(calculationDate);
			dailyDetail.setOpen(accountAmount);
			dailyDetail.setClose(accountAmount);
			if (null != dailyStockDetails.get(calculationDate)) {
				dailyDetail.setPerformancePercent(dailyStockDetails.get(calculationDate).getPerformancePercent());
				fakePerformancePercent = dailyStockDetails.get(calculationDate).getPerformancePercent();
			} else {
				dailyDetail.setPerformancePercent(fakePerformancePercent);
			}

			// also valid for the first time because the first entry of depotTransactions
			// must have the
			// same date as the daily stock list
			if (depotIndex < depotTransactionList.size()) {
				if (depotTransactionList.get(depotIndex).getActionDate().toLocalDate().isEqual(calculationDate)) {
					// consider payment
					accountAmount += depotTransactionList.get(depotIndex).getAmount();
					dailyDetail.setClose(accountAmount);
					depotIndex++;
				}
			}
			dailyDetails.add(dailyDetail);
			calculationDate = calculationDate.plusDays(1);
		}
		return dailyDetails;
	}

}
