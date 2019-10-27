package com.dam.portfolio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;
import com.dam.portfolio.model.PortfolioModel;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioPerformanceRequest;
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
	public long count() {
		return portfolioModel.count();
	}
	
	public PortfolioPerformance getPortfolioPerformance (PortfolioPerformanceRequest portfolioPerformanceRequest) throws DamServiceException {
		
	}

	public Portfolio getPortfolioSafe(PortfolioRequest portfolioRequest)
			throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioRequest, portfolioRequest.getRequestorUserId(),
				portfolioRequest.getRights());

		if (null == portfolioRequest.getPortfolioId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "portfolioId is not set.");
		}

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(portfolioRequest.getRequestorUserId(), null,
				portfolioRequest.getRights());

		Portfolio portfolio = getPortfolioById(
				portfolioRequest.getPortfolioId());

		if (null == portfolio) {
			throw new DamServiceException(new Long(404), "Portfolio Unknown",
					"Portfolio not found or invalid request");
		}

		return portfolio;
	}

	/**
	 * Creation of portfolio requests existing userId, givenName and
	 * lastName. userId in Entity Container mustn't be null
	 * 
	 * @param portfolioContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public Portfolio createPortfolioSafe(PortfolioCreateRequest portfolioCreateRequest)
			throws DamServiceException {
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
		Portfolio existingPortfolio = getPortfolioById(
				portfolioCreateRequest.getPortfolio().getPortfolioId());
		
		if (null == existingPortfolio) {
			existingPortfolio = getPortfolioByName(portfolioCreateRequest.getPortfolio().getPortfolioName());
		}

		if (null != existingPortfolio) {
			return updatePortfolio(existingPortfolio,
					portfolioCreateRequest.getPortfolio());
		}

		Portfolio portfolio;
		
		try {
		// save if all checks are ok and the portfolio doesn't exist
		portfolio = portfolioModel
				.save(portfolioCreateRequest.getPortfolio());
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
	public Portfolio updatePortfolioSafe(PortfolioUpdateRequest portfolioUpdateRequest)
			throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioUpdateRequest, portfolioUpdateRequest.getRequestorUserId(),
				portfolioUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(portfolioUpdateRequest.getPortfolio(), Portfolio.class, "Portfolio Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(portfolioUpdateRequest.getRequestorUserId(),
				null,
				portfolioUpdateRequest.getRights());

		// check if still exists
		Portfolio existingPortfolio = getPortfolioById(
				portfolioUpdateRequest.getPortfolio().getPortfolioId());

		// Portfolio must exist and userId ist not permutable
		if (null == existingPortfolio) {
			throw new DamServiceException(new Long(404), "Portfolio for update not found",
					"Portfolio with portfolioId doesn't exist.");
		}

		return updatePortfolio(existingPortfolio,
				portfolioUpdateRequest.getPortfolio());
	}

	/**
	 * Secure drop, user must be owner
	 * 
	 * @param userId
	 * @param portfolio
	 * @return
	 */
	public Long dropPortfolioSafe(PortfolioDropRequest portfolioDropRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(portfolioDropRequest, portfolioDropRequest.getRequestorUserId(), portfolioDropRequest.getRights());
		PermissionCheck.checkRequestedEntity(portfolioDropRequest.getPortfolio(), Portfolio.class, "Portfolio Class");

		// Save database requests
		PermissionCheck.isDeletePermissionSet(portfolioDropRequest.getRequestorUserId(),	null, portfolioDropRequest.getRights());

		Portfolio existingPortfolio = getPortfolioById(
				portfolioDropRequest.getPortfolio().getPortfolioId());

		if (null == existingPortfolio) {
			throw new DamServiceException(new Long(404), "Portfolio could not be dropped",
					"Portfolio does not exist or could not be found in database.");
		}

		if (200 == dropPortfolio(existingPortfolio)) {
			mapStore.dropMapEntriesByPortfolioId(portfolioDropRequest.getPortfolio().getPortfolioId());
		}
		
		return 200L;

	}
	
	/**
	 * Lists all Portfolios.
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

		Optional<Portfolio> optionalPortfolio = portfolioModel
				.findById(portfolioId);
		if (null != optionalPortfolio && optionalPortfolio.isPresent()) {
			return optionalPortfolio.get();
		}
		return null;
	}


	/**
	 * Update portfolio with changed values
	 * 
	 * @param portfolioForUpdate
	 * @param portfolioContainer
	 * @return
	 */
	private Portfolio updatePortfolio(Portfolio portfolioForUpdate,
			Portfolio portfolioContainer) throws DamServiceException {
		
		if (null != portfolioForUpdate && null != portfolioContainer) {
			portfolioForUpdate.updateEntity(portfolioContainer);
			try {
				return portfolioModel.save(portfolioForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409),
						"Portfolio could not be saved. Perhaps duplicate keys.", e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Portfolio could not be saved",
				"Check Portfolio data in request.");
	}

	private Long dropPortfolio(Portfolio portfolio) {
		if (null != portfolio) {
			portfolioModel.deleteById(portfolio.getPortfolioId());
			Portfolio deletedPortfolio = getPortfolioById(
					portfolio.getPortfolioId());
			if (null == deletedPortfolio) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

}
