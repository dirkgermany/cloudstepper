package com.dam.depot.store;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.dam.depot.PermissionCheck;
import com.dam.depot.model.DepotModel;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.performance.DepotPerformanceDetail;
import com.dam.depot.performance.PerformanceCalculator;
import com.dam.depot.performance.PortfolioPerformance;
import com.dam.depot.performance.StockQuotationDetail;
import com.dam.depot.rest.consumer.Client;
import com.dam.depot.rest.message.performance.DaysToGoalResponse;
import com.dam.depot.rest.message.performance.DepotPerformanceResponse;
import com.dam.depot.types.ActionType;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class DepotStore {

	@Autowired
	private DepotModel depotModel;

	@Autowired
	private DepotTransactionStore depotTransactionStore;
	
	@Autowired
	private AccountStatusStore accountStatusStore;

	@Autowired
	private Client client;

	public long count() {
		return depotModel.count();
	}

	public List<Depot> getDepotByUserId(Long userId) {
		return depotModel.findByUserId(userId);
	}

	public Depot saveDepot(Depot depot) throws DamServiceException {
		return depotModel.save(depot);
	}

	public Depot getDepotByUserPortfolio(Long userId, Long portfolioId) {
		return depotModel.findByUserPortfolio(userId, portfolioId);
	}
	
	/**
	 * Summarizes all investments and calculates the days until the goal is reached.
	 * Formula: (goalValue - depotValue) / (ROI / daysOfPeriod + savingFactor) 
	 * @param params
	 * @param tokenId
	 * @return
	 * @throws DamServiceException
	 */
	public DaysToGoalResponse calcDaysToGoalSafe(Map<String, String> params, String tokenId) throws DamServiceException {
		PermissionCheck.checkRequestedParams(params.get("requestorUserId"), params.get("rights"));
		Long requestorUserId = extractLong(params.get("requestorUserId"));
		

		Long userId = extractLong(params.get("userId"));

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(requestorUserId, userId, params.get("rights"));
		
		Float goalAmount = extractFloat(params.get("goalAmount"));
		if (null == goalAmount) {
			throw new DamServiceException(404L, "Missing parameter goalAmount", "goalAmount must be part of the request");
		}
		
		String sF = params.get("savingFactor");
		if (null == sF) {
			sF = "0";
		}
		Float savingFactor = extractFloat(sF);
		
		DepotPerformanceResponse response = getDepotPerformance(params, tokenId);
		if (null == response || null == response.getDepotPerformanceDetails() || response.getDepotPerformanceDetails().size() == 0)  {
			throw new DamServiceException(410L, "Calculation not possible", "Not enough values, perhaps period too short");
		}
		
		Float invest = response.getDepotPerformanceDetails().get(response.getDepotPerformanceDetails().size()-1).getInvest();
		LocalDate firstInvestDate = response.getDepotPerformanceDetails().get(0).getStartDate();
		LocalDate lastInvestDate = response.getDepotPerformanceDetails().get(response.getDepotPerformanceDetails().size()-1).getStartDate();
//		Integer daysOfPeriod = Period.between(firstInvestDate, lastInvestDate).getDays();
		
		long daysOfPeriod = ChronoUnit.DAYS.between(firstInvestDate, lastInvestDate);
		

		Float depotValue = response.getPeriodAmountAtEnd();
		Float ROI = response.getPeriodPerformanceValue();
		
		Float daysToGoalFloat = (goalAmount - depotValue) / (ROI / daysOfPeriod + savingFactor);
		Integer daysToGoal = daysToGoalFloat.intValue();
		
		if (daysToGoal < 0) {
			throw new DamServiceException(410L, "Calculation not possible", "The saving rate cannot countervail the negative portfolio performance");
		}
		
		LocalDate dateOfGoal =  LocalDate.now().plusDays(daysToGoal);

		return new DaysToGoalResponse(200L, "OK", "Days until reaching goal calculated", daysToGoal, dateOfGoal, invest, ROI, depotValue);

	}
	

	public DepotPerformanceResponse getDepotPerformanceSafe(Map<String, String> params, String tokenId)
			throws DamServiceException {

		PermissionCheck.checkRequestedParams(params.get("requestorUserId"), params.get("rights"));
		Long requestorUserId = extractLong(params.get("requestorUserId"));
//		Long portfolioId = extractLong(params.get("portfolioId"));
		Long userId = extractLong(params.get("userId"));

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(requestorUserId, userId, params.get("rights"));
		
		return getDepotPerformance(params, tokenId);

	}

	private DepotPerformanceResponse getDepotPerformance (Map<String, String> params, String tokenId)
			throws DamServiceException {
		Long portfolioId = extractLong(params.get("portfolioId"));
		Long userId = extractLong(params.get("userId"));

		// List of depotTransactions of user and portfolio
		// Ordered by action_date (date when investor did something with his depot)
		List<DepotTransaction> depotTransactionList = depotTransactionStore
				.getDepotTransactionListByUserPortfolio(userId, portfolioId);
		if (null == depotTransactionList || depotTransactionList.isEmpty()) {
			return null;
		}

		// if start date not given by GET param use first entry of depot investments
		// the same with end date - if not part of the request use yesterday
		LocalDate startDate = extractDate(params.get("startDate"));
		LocalDate endDate = extractDate(params.get("endDate"));

		String period = params.get("period");
		if (null != period) {
			LocalDate today = LocalDate.now();
			period = period.trim().toUpperCase();
			switch (period) {
			case "LAST_YEAR":
				today = today.minusYears(1);
				startDate = today.withDayOfYear(1);
				endDate = today.withDayOfYear(today.lengthOfYear());
				break;
			case "LAST_MONTH":
				today = today.minusMonths(1);
				startDate = today.withDayOfMonth(1);
				endDate = today.withDayOfMonth(today.lengthOfMonth());
				break;
			case "LAST_WEEK":
				today = today.minusWeeks(1);
				startDate = today.with(DayOfWeek.MONDAY);
				endDate = today.with(DayOfWeek.FRIDAY);
				break;
			case "THIS_YEAR":
				startDate = today.withDayOfYear(1);
				endDate = LocalDate.now().minusDays(1);
				break;
			case "THIS_MONTH":
				startDate = today.withDayOfMonth(1);
				endDate = LocalDate.now().minusDays(1);
				break;
			case "THIS_WEEK":
				startDate = today.with(DayOfWeek.MONDAY);
				endDate = LocalDate.now().minusDays(1);
				break;

			default:
				break;
			}
			
		}

		if (null == startDate) {
			startDate = depotTransactionList.get(0).getActionDate().toLocalDate();
		}

		if (startDate.isAfter(LocalDateTime.now().minusDays(1).toLocalDate())) {
			throw new DamServiceException(404L, "Invalid start date",
					"The first day of invest was today but must be before today or earlier");
		}

		if (null == endDate) {
			endDate = LocalDateTime.now().minusDays(1).toLocalDate();
		}

		// last but not least
		// ensure that start date is not before the first invest of user
		if (startDate.isBefore(depotTransactionList.get(0).getActionDate().toLocalDate())) {
			startDate = depotTransactionList.get(0).getActionDate().toLocalDate();
		}

		if (endDate.isBefore(startDate)) {
			endDate = startDate;
		}

		PortfolioPerformance portfolioPerformance = client.readPortfolioPerformance(userId, portfolioId, startDate,
				endDate, tokenId);
		Map<LocalDate, StockQuotationDetail> dailyStockDetails = portfolioPerformance.getDailyDetails();

		List<DepotPerformanceDetail> depotPerformanceDetails = new PerformanceCalculator()
				.calculateDepotPerformance(startDate, endDate, depotTransactionList, dailyStockDetails);

		Float periodPerformancePercentage = 0F;
		Float periodPerformanceValue = 0F;
		Float periodAmountAtEnd = 0F;
		String periodPerformancePercentageAsString = "0.00%";
		if (null != depotPerformanceDetails && !depotPerformanceDetails.isEmpty()) {
			DepotPerformanceDetail lastDetail = depotPerformanceDetails.get(depotPerformanceDetails.size() - 1);
			periodPerformancePercentage = lastDetail.getPerformancePercent();
			periodPerformancePercentageAsString = lastDetail.getPerformanceAsString();
			periodPerformanceValue = lastDetail.getAmountAtEnd() - lastDetail.getInvest();
			periodAmountAtEnd = lastDetail.getAmountAtEnd();
		}
		return new DepotPerformanceResponse(200L, "OK", "Depot entries found", periodPerformancePercentage,
				periodPerformancePercentageAsString, periodPerformanceValue, periodAmountAtEnd, depotPerformanceDetails);
	}

	private LocalDate extractDate(String dateString) throws DamServiceException {
		try {
			return LocalDate.parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}
	
	private Float extractFloat (String floatString) throws DamServiceException {
		try {
			return Float.valueOf(floatString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction Float from String failed",
					"Parameter is required but null or does not represent a Long value");
		}		
	}

	private Long extractLong(String longString) throws DamServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}
}
