package com.dam.depot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.model.DepotModel;
import com.dam.depot.model.entity.Account;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.account.AccountRequest;
import com.dam.depot.rest.message.depot.DepotListRequest;
import com.dam.depot.rest.message.depot.DepotRequest;
import com.dam.depot.types.ActionType;
import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;

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
	private AccountStore accountStore;

	public long count() {
		return depotModel.count();
	}

	/**
	 * Save getter for Depot. request
	 * 
	 * @param depot
	 * @return
	 */
	public Depot getDepotSafe(DepotRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepot().getDepotId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Depot ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		// Read attemptions
		Depot depot = getDepotById(depotRequest.getDepot().getDepotId());
		if (null == depot) {
			throw new DamServiceException(new Long(404), "Depot Unknown", "Depot not found or invalid request");
		}

		return depot;
	}

	/**
	 * Delivers a list of depot entries by filtering the params.
	 * 
	 * @param depotRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<Depot> getDepotListSafe(DepotListRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepot()) {
			throw new DamServiceException(500L, "Invalid request", "Depot is not set in request.");
		}
		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		List<Depot> depotList = null;
		// search by sql and indexed fields
		if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateFrom() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateFromDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateFrom(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserActionDateFrom(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserDateFrom(depotRequest.getDepot().getUserId(),
					depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()) {
			depotList = getDepotListByUserAction(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction());
		} else if (null != depotRequest.getDepot().getUserId()) {
			depotList = getDepotListByUser(depotRequest.getDepot().getUserId());
		}

		if (null == depotList) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		// Filter
		if (null == depotRequest.getFilter() || !depotRequest.getFilter().isFiltered()) {
			return depotList;
		}

		List<Depot> filteredDepotList = new ArrayList<>();
		Iterator<Depot> it = depotList.iterator();
		while (it.hasNext()) {
			Depot depot = it.next();
			if (null != depotRequest.getFilter().getAmountFrom()
					&& depot.getAmount() < depotRequest.getFilter().getAmountFrom()) {
				continue;
			}
			if (null != depotRequest.getFilter().getAmountUntil()
					&& depot.getAmount() > depotRequest.getFilter().getAmountUntil()) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateFrom()
					&& depot.getActionDate().before(depotRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateUntil()
					&& depot.getActionDate().after(depotRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != depotRequest.getFilter().getEventText() && !depot.getEventText().toUpperCase()
					.contains(depotRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredDepotList.add(depot);
		}

		if (filteredDepotList.isEmpty()) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		return filteredDepotList;
	}

	/**
	 * Transfer From Depot To User Account (minus value) - transfer between both
	 * money accounts - send information to Depot Bank
	 * 
	 * @param depotRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Depot transferToAccountSafe(DepotRequest depotRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.DEPOT_TRANSFER);
		RequestHelper.checkActions(depotRequest.getDepot().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(depotRequest.getDepot().getAmount());
		RequestHelper.checkCurrency(depotRequest.getDepot().getCurrency());

		Float amount = RequestHelper.setAmountNegative(depotRequest.getDepot().getAmount());
		depotRequest.getDepot().setAmount(amount);
		depotRequest.getDepot().setActionDate(new Date());

		// store depot
		Depot depot = createDepotSafe(depotRequest);

		Account account = new Account(depotRequest.getDepot());
		amount = RequestHelper.setAmountPositive(amount);
		AccountRequest accountRequest = new AccountRequest(account.getRequestorUserId(), account);

		// store account
		accountStore.createAccountSafe(accountRequest);

		return depot;
	}

	/**
	 * Deposit is only stored to the Entity Intent
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Depot depositSafe(DepotRequest depotDepositRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.DEPOSIT);
		RequestHelper.checkActions(depotDepositRequest.getDepot().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(depotDepositRequest.getDepot().getAmount());
		RequestHelper.checkCurrency(depotDepositRequest.getDepot().getCurrency());

		Account account = new Account(depotDepositRequest.getDepot());
		account.setAction(ActionType.DEPOT_INVEST_INTENT);
		account.setActionDate(new Date());
		AccountRequest accountRequest = new AccountRequest(account.getRequestorUserId(), account);
		account = accountStore.createAccountSafe(accountRequest);

		return new Depot(account);
	}

	/**
	 * Creation of depot requests existing userId, givenName and lastName. userId in
	 * Entity Container mustn't be null
	 * 
	 * @param depotContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	private Depot createDepotSafe(DepotRequest depotCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(depotCreateRequest, depotCreateRequest.getRequestorUserId(),
				depotCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(depotCreateRequest.getDepot(), Depot.class, "Depot Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(depotCreateRequest.getRequestorUserId(), null,
				depotCreateRequest.getRights());

		if (null == depotCreateRequest.getDepot().getAction() || null == depotCreateRequest.getDepot().getActionDate()
				|| null == depotCreateRequest.getDepot().getAmount()
				|| null == depotCreateRequest.getDepot().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Depot data not complete");
		}

		Depot depot;
		try {
			depot = depotModel.save(depotCreateRequest.getDepot());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Depot could not be stored", e.getMessage());
		}

		if (null == depot) {
			throw new DamServiceException(new Long(422), "Depot not created",
					"Depot still exists, data invalid or not complete");
		}
		return depot;

	}

	/**
	 * Lists all Depots.
	 * 
	 * @return
	 */
	public List<Depot> getDepotList() {
		List<Depot> depots = new ArrayList<>();
		Iterator<Depot> it = depotModel.findAll().iterator();
		while (it.hasNext()) {
			depots.add(it.next());
		}
		return depots;
	}

	private List<Depot> getDepotListByUserAction(Long userId, ActionType action) {
		return depotModel.findByUserAction(userId, action);
	}

	private List<Depot> getDepotListByUserDateFrom(Long userId, Date dateFrom) {
		return depotModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<Depot> getDepotListByUserDateUntil(Long userId, Date dateUntil) {
		return depotModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<Depot> getDepotListByUserActionDateFrom(Long userId, ActionType action, Date dateFrom) {
		return depotModel.findByUserActionDateFrom(userId, action, dateFrom);
	}

	private List<Depot> getDepotListByUserActionDateUntil(Long userId, ActionType action, Date dateUntil) {
		return depotModel.findByUserActionDateUntil(userId, action, dateUntil);
	}

	private List<Depot> getDepotListByUserActionDateFromDateUntil(Long userId, ActionType action, Date dateFrom,
			Date dateUntil) {
		return depotModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	private List<Depot> getDepotListByUser(Long userId) {
		return depotModel.findDepotByUser(userId);
	}

	public Depot getDepotById(Long depotId) {
		if (null == depotId) {
			return null;
		}

		Optional<Depot> optionalDepot = depotModel.findById(depotId);
		if (null != optionalDepot && optionalDepot.isPresent()) {
			return optionalDepot.get();
		}
		return null;
	}

	public Long dropDepot(Depot depot) {
		if (null != depot) {
			depotModel.deleteById(depot.getDepotId());
			Depot deletedDepot = getDepotById(depot.getDepotId());
			if (null == deletedDepot) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

}
