package com.dam.depot.store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.dam.depot.PermissionCheck;
import com.dam.depot.RequestHelper;
import com.dam.depot.model.DepotTransactionModel;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionRequest;
import com.dam.depot.types.ActionType;
import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Component
public class DepotTransactionStore {

	@Autowired
	private DepotTransactionModel depotTransactionModel;

	public long count() {
		return depotTransactionModel.count();
	}

	/**
	 * Save getter for DepotTransactions. request
	 * 
	 * @param depot
	 * @return
	 */
	public DepotTransaction getDepotTransactionSafe(DepotTransactionRequest depotTransactionRequest) throws DamServiceException {
		if (null == depotTransactionRequest.getDepotTransaction().getDepotTransactionId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Depot ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(depotTransactionRequest, depotTransactionRequest.getRequestorUserId(), depotTransactionRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotTransactionRequest.getRequestorUserId(), null, depotTransactionRequest.getRights());

		// Read attemptions
		DepotTransaction depotTransaction = getDepotTransactionById(depotTransactionRequest.getDepotTransaction().getDepotTransactionId());
		if (null == depotTransaction) {
			throw new DamServiceException(new Long(404), "Depot Unknown", "Depot not found or invalid request");
		}

		return depotTransaction;
	}

	/**
	 * Delivers a list of depot entries by filtering the params.
	 * 
	 * @param depotRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<DepotTransaction> getDepotTransactionListSafe(DepotTransactionListRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepotTransaction()) {
			throw new DamServiceException(500L, "Invalid request", "Depot is not set in request.");
		}
		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		List<DepotTransaction> depotList = null;
		// search by sql and indexed fields
		if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateFrom() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotTransactionListByUserActionDateFromDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateFrom(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotTransactionListByUserActionDateFrom(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotTransactionListByUserActionDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotTransactionListByUserDateFrom(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotTransactionListByUserDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()) {
			depotList = getDepotTransactionListByUserAction(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction());
		} else if (null != depotRequest.getDepotTransaction().getUserId()) {
			depotList = getDepotTransactionListByUser(depotRequest.getDepotTransaction().getUserId());
		}

		if (null == depotList) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		// Filter
		if (null == depotRequest.getFilter() || !depotRequest.getFilter().isFiltered()) {
			return depotList;
		}

		List<DepotTransaction> filteredDepotTransactionList = new ArrayList<>();
		Iterator<DepotTransaction> it = depotList.iterator();
		while (it.hasNext()) {
			DepotTransaction depotTransaction = it.next();
			if (null != depotRequest.getFilter().getAmountFrom()
					&& depotTransaction.getAmount() < depotRequest.getFilter().getAmountFrom()) {
				continue;
			}
			if (null != depotRequest.getFilter().getAmountUntil()
					&& depotTransaction.getAmount() > depotRequest.getFilter().getAmountUntil()) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateFrom()
					&& depotTransaction.getActionDate().isBefore(depotRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateUntil()
					&& depotTransaction.getActionDate().isAfter(depotRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != depotRequest.getFilter().getEventText() && !depotTransaction.getEventText().toUpperCase()
					.contains(depotRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredDepotTransactionList.add(depotTransaction);
		}

		if (filteredDepotTransactionList.isEmpty()) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		return filteredDepotTransactionList;
	}

	/**
	 * Transfer From Depot To User Account (minus value) - transfer between both
	 * money accounts - send information to Depot Bank
	 * 
	 * @param depotTransactionRequest
	 * @return
	 * @throws DamServiceException
	 */
	public DepotTransaction transferToAccountSafe(DepotTransactionRequest depotTransactionRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.DEPOT_TRANSFER);
		RequestHelper.checkActions(depotTransactionRequest.getDepotTransaction().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(depotTransactionRequest.getDepotTransaction().getAmount());
		RequestHelper.checkCurrency(depotTransactionRequest.getDepotTransaction().getCurrency());

		Float amount = RequestHelper.setAmountNegative(depotTransactionRequest.getDepotTransaction().getAmount());
		depotTransactionRequest.getDepotTransaction().setAmount(amount);
		depotTransactionRequest.getDepotTransaction().setActionDate(LocalDateTime.now());

		// store depot
		DepotTransaction depotTransaction = createDepotTransactionSafe(depotTransactionRequest);		
		//TODO!!!
		
		amount = RequestHelper.setAmountPositive(amount);
//		AccountRequest accountRequest = new AccountRequest(account.getRequestorUserId(), account);

		// store account
//		accountStore.createAccountSafe(accountRequest);

		return depotTransaction;
	}


	/**
	 * Creation of depot requests existing userId, givenName and lastName. userId in
	 * Entity Container mustn't be null
	 * 
	 * @param depotContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	private DepotTransaction createDepotTransactionSafe(DepotTransactionRequest depotCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(depotCreateRequest, depotCreateRequest.getRequestorUserId(),
				depotCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(depotCreateRequest.getDepotTransaction(), DepotTransaction.class, "Depot Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(depotCreateRequest.getRequestorUserId(), null,
				depotCreateRequest.getRights());

		if (null == depotCreateRequest.getDepotTransaction().getAction() || null == depotCreateRequest.getDepotTransaction().getActionDate()
				|| null == depotCreateRequest.getDepotTransaction().getAmount()
				|| null == depotCreateRequest.getDepotTransaction().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Depot data not complete");
		}

		DepotTransaction depotTransaction;
		try {
			depotTransaction = depotTransactionModel.save(depotCreateRequest.getDepotTransaction());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Depot could not be stored", e.getMessage());
		}

		if (null == depotTransaction) {
			throw new DamServiceException(new Long(422), "Depot not created",
					"Depot still exists, data invalid or not complete");
		}
		return depotTransaction;

	}

	/**
	 * Lists all Depots.
	 * 
	 * @return
	 */
	public List<DepotTransaction> getDepotTransactionList() {
		List<DepotTransaction> depotTransaction = new ArrayList<>();
		Iterator<DepotTransaction> it = depotTransactionModel.findAll().iterator();
		while (it.hasNext()) {
			depotTransaction.add(it.next());
		}
		return depotTransaction;
	}

	private List<DepotTransaction> getDepotTransactionListByUserAction(Long userId, ActionType action) {
		return depotTransactionModel.findByUserAction(userId, action);
	}

	private List<DepotTransaction> getDepotTransactionListByUserDateFrom(Long userId, LocalDateTime dateFrom) {
		return depotTransactionModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<DepotTransaction> getDepotTransactionListByUserDateUntil(Long userId, LocalDateTime dateUntil) {
		return depotTransactionModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<DepotTransaction> getDepotTransactionListByUserActionDateFrom(Long userId, ActionType action, LocalDateTime dateFrom) {
		return depotTransactionModel.findByUserActionDateFrom(userId, action, dateFrom);
	}

	private List<DepotTransaction> getDepotTransactionListByUserActionDateUntil(Long userId, ActionType action, LocalDateTime dateUntil) {
		return depotTransactionModel.findByUserActionDateUntil(userId, action, dateUntil);
	}

	private List<DepotTransaction> getDepotTransactionListByUserActionDateFromDateUntil(Long userId, ActionType action, LocalDateTime dateFrom,
			LocalDateTime dateUntil) {
		return depotTransactionModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	public List<DepotTransaction> getDepotTransactionListByUserPortfolio(Long userId, Long portfolioId) {
		return depotTransactionModel.findByUserPortfolio(userId, portfolioId);
	}

	private List<DepotTransaction> getDepotTransactionListByUser(Long userId) {
		return depotTransactionModel.findDepotTransactionByUser(userId);
	}

	public DepotTransaction getDepotTransactionById(Long depotId) {
		if (null == depotId) {
			return null;
		}

		Optional<DepotTransaction> optionalDepot = depotTransactionModel.findById(depotId);
		if (null != optionalDepot && optionalDepot.isPresent()) {
			return optionalDepot.get();
		}
		return null;
	}

	public Long dropDepotTransaction(DepotTransaction depotTransaction) {
		if (null != depotTransaction) {
			depotTransactionModel.deleteById(depotTransaction.getDepotTransactionId());
			DepotTransaction deletedDepot = getDepotTransactionById(depotTransaction.getDepotTransactionId());
			if (null == deletedDepot) {
				return new Long(200);
			}
		}

		return new Long(10);
	}
	
	public DepotTransaction storeDepotTransaction(DepotTransaction depotTransaction) {
		return depotTransactionModel.save(depotTransaction);
	}

}
