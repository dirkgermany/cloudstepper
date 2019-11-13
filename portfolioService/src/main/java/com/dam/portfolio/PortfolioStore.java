package com.dam.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;
import com.dam.portfolio.model.PortfolioModel;
import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.model.entity.StockHistory;
import com.dam.portfolio.performance.AssetPerformance;
import com.dam.portfolio.performance.PortfolioPerformance;
import com.dam.portfolio.performance.PortfolioPerformanceCalculator;
import com.dam.portfolio.rest.consumer.Client;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioPerformanceRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioPerformanceResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioUpdateRequest;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class PortfolioStore {

	@Autowired
	private PortfolioModel portfolioModel;

	@Autowired
	private AssetClassToPortfolioMapStore mapStore;

	@Autowired
	private PortfolioPerformanceCalculator calculator;

	@Autowired
	private Client client;

	public long count() {
		return portfolioModel.count();
	}

	public PortfolioPerformanceResponse getPortfolioPerformanceSafe(Map<String, String> params, String tokenId)
			throws DamServiceException {

		PermissionCheck.checkRequestedParams(params.get("requestorUserId"), params.get("rights"));
		Long requestorUserId = extractLong(params.get("requestorUserId"));

		Long portfolioId = extractLong(params.get("portfolioId"));
		LocalDate startDate = extractDate(params.get("startDate"));
		LocalDate endDate = extractDate(params.get("endDate"));

		Long assetClassId = extractLong(params.get("assetClassId"), false);

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(requestorUserId, null, params.get("rights"));

		// Portfolio and assets
		Map<AssetClass, List<StockHistory>> assetStockHistory = new HashMap<>();

		ConstructionMap map = mapStore.getConstructionMap(portfolioId);
		Portfolio portfolio = map.getPortfolio();
		Iterator<AssetClass> it = map.getAssetClasses().iterator();
		while (it.hasNext()) {
			AssetClass asset = it.next();
			StockHistory history = new StockHistory();
			history.setSymbol(asset.getSymbol());
			history.setWkn(asset.getWkn());

			List<StockHistory> historyList = client.readAssetStockHistory(history, startDate, endDate, tokenId);
			assetStockHistory.put(asset, historyList);
		}

		calculator.generatePerformanceLists(portfolio, assetStockHistory, startDate, endDate);
		PortfolioPerformance portfolioPerformance = null;
		List<AssetPerformance> assetPerformanceList = null;
		AssetPerformance assetPerformance = null;

		portfolioPerformance = calculator.getPortfolioPerformance();

		if (extractBoolean(params.get("showAllAssetsOfPortfolio"), false)) {
			assetPerformanceList = calculator.getAssetPerformanceList();
		}

		Boolean showAssetPerformance = extractBoolean(params.get("showAssetPerformance"), false);
		if (null != assetClassId && showAssetPerformance) {
			assetPerformance = calculator.getAssetPerformance(assetClassId);
		}

		return new PortfolioPerformanceResponse(200L, "OK", "Performance calculated", portfolioPerformance,
				assetPerformance, assetPerformanceList);

	}

	public PortfolioPerformanceResponse getPortfolioPerformanceSafe(PortfolioPerformanceRequest request, String tokenId)
			throws DamServiceException {
		Map<String, String> params = new HashMap<>();
		params.put("rights", request.getRights());
		params.put("assetClassId",  null == request.getAssetClassId()? null : request.getAssetClassId().toString());
		params.put("portfolioId",  null == request.getPortfolioId()? null : request.getPortfolioId().toString());
		params.put("clientSource", request.getClientSource());
		params.put("requestorUserId", null == request.getRequestorUserId()? null : request.getRequestorUserId().toString());
		params.put("startDate", null == request.getStartDate()? null : request.getStartDate().toString());
		params.put("endDate", null == request.getEndDate()? null : request.getEndDate().toString());
		return getPortfolioPerformanceSafe(params, tokenId);
	}

	public Portfolio getPortfolioSafe(PortfolioRequest portfolioRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioRequest, portfolioRequest.getRequestorUserId(),
				portfolioRequest.getRights());

		if (null == portfolioRequest.getPortfolioId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "portfolioId is not set.");
		}

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(portfolioRequest.getRequestorUserId(), null, portfolioRequest.getRights());

		Portfolio portfolio = getPortfolioById(portfolioRequest.getPortfolioId());

		if (null == portfolio) {
			throw new DamServiceException(new Long(404), "Portfolio Unknown", "Portfolio not found or invalid request");
		}

		return portfolio;
	}

	/**
	 * Creation of portfolio requests existing userId, givenName and lastName.
	 * userId in Entity Container mustn't be null
	 * 
	 * @param portfolioContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public Portfolio createPortfolioSafe(PortfolioCreateRequest portfolioCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioCreateRequest, portfolioCreateRequest.getRequestorUserId(),
				portfolioCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(portfolioCreateRequest.getPortfolio(), Portfolio.class, "Portfolio Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(portfolioCreateRequest.getRequestorUserId(), null,
				portfolioCreateRequest.getRights());

		if (null == portfolioCreateRequest.getPortfolio().getRisc()
				|| null == portfolioCreateRequest.getPortfolio().getCallMoneyPct()
				|| null == portfolioCreateRequest.getPortfolio().getGoldPct()
				|| null == portfolioCreateRequest.getPortfolio().getLoanPct()
				|| null == portfolioCreateRequest.getPortfolio().getSharePct()
				|| null == portfolioCreateRequest.getPortfolio().getValidFrom()
				|| null == portfolioCreateRequest.getPortfolio().getValidUntil()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Portfolio data not complete");
		}

		// check if still exists
		// direct by portfolioId
		Portfolio existingPortfolio = getPortfolioById(portfolioCreateRequest.getPortfolio().getPortfolioId());

		if (null == existingPortfolio) {
			existingPortfolio = getPortfolioByName(portfolioCreateRequest.getPortfolio().getPortfolioName());
		}

		if (null != existingPortfolio) {
			return updatePortfolio(existingPortfolio, portfolioCreateRequest.getPortfolio());
		}

		Portfolio portfolio;

		try {
			// save if all checks are ok and the portfolio doesn't exist
			portfolio = portfolioModel.save(portfolioCreateRequest.getPortfolio());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Portfolioconstruction could not be stored", e.getMessage());
		}

		if (null == portfolio) {
			throw new DamServiceException(new Long(422), "Portfolio not created",
					"Portfolio still exists, data invalid or not complete");
		}
		return portfolio;

	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public Portfolio updatePortfolioSafe(PortfolioUpdateRequest portfolioUpdateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioUpdateRequest, portfolioUpdateRequest.getRequestorUserId(),
				portfolioUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(portfolioUpdateRequest.getPortfolio(), Portfolio.class, "Portfolio Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(portfolioUpdateRequest.getRequestorUserId(), null,
				portfolioUpdateRequest.getRights());

		// check if still exists
		Portfolio existingPortfolio = getPortfolioById(portfolioUpdateRequest.getPortfolio().getPortfolioId());

		// Portfolio must exist and userId ist not permutable
		if (null == existingPortfolio) {
			throw new DamServiceException(new Long(404), "Portfolio for update not found",
					"Portfolio with portfolioId doesn't exist.");
		}

		return updatePortfolio(existingPortfolio, portfolioUpdateRequest.getPortfolio());
	}

	/**
	 * Secure drop, user must be owner
	 * 
	 * @param userId
	 * @param portfolio
	 * @return
	 */
	public Long dropPortfolioSafe(PortfolioDropRequest portfolioDropRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioDropRequest, portfolioDropRequest.getRequestorUserId(),
				portfolioDropRequest.getRights());
		PermissionCheck.checkRequestedEntity(portfolioDropRequest.getPortfolio(), Portfolio.class, "Portfolio Class");

		// Save database requests
		PermissionCheck.isDeletePermissionSet(portfolioDropRequest.getRequestorUserId(), null,
				portfolioDropRequest.getRights());

		Portfolio existingPortfolio = getPortfolioById(portfolioDropRequest.getPortfolio().getPortfolioId());

		if (null == existingPortfolio) {
			throw new DamServiceException(new Long(404), "Portfolio could not be dropped",
					"Portfolio does not exist or could not be found in database.");
		}

		if (200 == dropPortfolio(existingPortfolio)) {
			mapStore.dropMapEntriesByPortfolioId(portfolioDropRequest.getPortfolio().getPortfolioId());
			dropPortfolio(existingPortfolio);
		}

		return 200L;

	}

	/**
	 * Lists all Portfolios.
	 * 
	 * @return
	 */
	public List<Portfolio> getPortfolioList() {
		List<Portfolio> portfolios = new ArrayList<>();
		Iterator<Portfolio> it = portfolioModel.findAll().iterator();
		while (it.hasNext()) {
			portfolios.add(it.next());
		}
		return portfolios;
	}

	private Portfolio getPortfolioByName(String portfolioName) {
		return portfolioModel.findByPortfolioName(portfolioName);
	}

	public Portfolio getPortfolioById(Long portfolioId) {
		if (null == portfolioId) {
			return null;
		}

		Optional<Portfolio> optionalPortfolio = portfolioModel.findById(portfolioId);
		if (null != optionalPortfolio && optionalPortfolio.isPresent()) {
			return optionalPortfolio.get();
		}
		return null;
	}

	private Long extractLong(String longString, Boolean... required) throws DamServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			if (required.length >0 && required[0].booleanValue()) {
				throw new DamServiceException(404L, "Extraction Long from String failed",
						"Parameter is required but null or does not represent a Long value");
			} else
				return null;
		}
	}

	private LocalDate extractDate(String dateString) throws DamServiceException {
		try {
			return LocalDate.parse(dateString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction LocalDate from String failed",
					"Parameter is required but null or does not represent a LocalDate value");
		}
	}

	private Boolean extractBoolean(String booleanString, Boolean defaultValue) throws DamServiceException {
		if (null == booleanString || booleanString.isEmpty()) {
			return defaultValue;
		}

		try {
			return Boolean.valueOf(booleanString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction LocalDate from String failed",
					"Parameter is required but null or does not represent a LocalDate value");
		}
	}

	/**
	 * Update portfolio with changed values
	 * 
	 * @param portfolioForUpdate
	 * @param portfolioContainer
	 * @return
	 */
	private Portfolio updatePortfolio(Portfolio portfolioForUpdate, Portfolio portfolioContainer)
			throws DamServiceException {

		if (null != portfolioForUpdate && null != portfolioContainer) {
			portfolioForUpdate.updateEntity(portfolioContainer);
			try {
				return portfolioModel.save(portfolioForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "Portfolio could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Portfolio could not be saved",
				"Check Portfolio data in request.");
	}

	private Long dropPortfolio(Portfolio portfolio) {
		if (null != portfolio) {
			portfolioModel.deleteById(portfolio.getPortfolioId());
			Portfolio deletedPortfolio = getPortfolioById(portfolio.getPortfolioId());
			if (null == deletedPortfolio) {
				return new Long(200);
			}
		}

		return new Long(404);
	}

}
