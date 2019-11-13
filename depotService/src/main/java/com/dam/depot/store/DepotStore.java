package com.dam.depot.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.dam.depot.model.DepotModel;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.performance.DepotPerformanceDetail;
import com.dam.depot.performance.PerformanceCalculator;
import com.dam.depot.performance.PortfolioPerformance;
import com.dam.depot.performance.StockQuotationDetail;
import com.dam.depot.rest.consumer.Client;
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

	public DepotPerformanceResponse getDepotPerformanceSafe(Map<String, String> params)
			throws DamServiceException {
		
		// userId
		Long userId = Long.valueOf(params.get("userId"));
		Long portfolioId = Long.valueOf(params.get("portfolioId"));
		

		// List of depotTransactions of user and portfolio
		// Ordered by action_date (date when investor did something with his depot)
		List<DepotTransaction> depotTransactionList = depotTransactionStore.getDepotTransactionListByUserPortfolio(userId, portfolioId);
		if (null == depotTransactionList || depotTransactionList.isEmpty()) {
			return null;
		}

		LocalDateTime startDate = depotTransactionList.get(0).getActionDate();
		LocalDateTime endDate = LocalDateTime.now().minusDays(1);
		PortfolioPerformance portfolioPerformance = client.readPortfolioPerformance(userId, portfolioId, startDate.toLocalDate(), endDate.toLocalDate());
		Map<LocalDate, StockQuotationDetail> dailyStockDetails = portfolioPerformance.getDailyDetails();
		
		List<DepotPerformanceDetail> depotPerformanceDetails = PerformanceCalculator.calculateDepotPerformance(startDate.toLocalDate(), endDate.toLocalDate(), depotTransactionList, dailyStockDetails);
		return new DepotPerformanceResponse(200L, "OK", "Depot entries found", depotPerformanceDetails);
	}

}
