package com.dam.depot;

import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
@Controller
public class DepotTransactionStore {

	@Autowired
	private DepotTransactionModel depotTransactionModel;

	@Autowired
	private AccountTransactionStore accountTransactionStore;

	public long count() {
		return depotTransactionModel.count();
	}

	/**
	 * Save getter for DepotTransactions. request
	 * 
	 * @param depot
	 * @return
	 */
	public DepotTransaction getDepotSafe(DepotTransactionRequest depotTransactionRequest) throws DamServiceException {
		if (null == depotTransactionRequest.getDepotTransaction().getDepotTransactionId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Depot ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(depotTransactionRequest, depotTransactionRequest.getRequestorUserId(), depotTransactionRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotTransactionRequest.getRequestorUserId(), null, depotTransactionRequest.getRights());

		// Read attemptions
		DepotTransaction depotTransaction = getDepotById(depotTransactionRequest.getDepotTransaction().getDepotTransactionId());
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
	public List<DepotTransaction> getDepotListSafe(DepotTransactionListRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepotTransaction()) {
			throw new DamServiceException(500L, "Invalid request", "Depot is not set in request.");
		}
		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		List<DepotTransaction> depotList = null;
		// search by sql and indexed fields
		if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateFrom() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateFromDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateFrom(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserActionDateFrom(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()
				&& null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction(), depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserDateFrom(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserDateUntil(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepotTransaction().getUserId() && null != depotRequest.getDepotTransaction().getAction()) {
			depotList = getDepotListByUserAction(depotRequest.getDepotTransaction().getUserId(),
					depotRequest.getDepotTransaction().getAction());
		} else if (null != depotRequest.getDepotTransaction().getUserId()) {
			depotList = getDepotListByUser(depotRequest.getDepotTransaction().getUserId());
		}

		if (null == depotList) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		// Filter
		if (null == depotRequest.getFilter() || !depotRequest.getFilter().isFiltered()) {
			return depotList;
		}

		List<DepotTransaction> filteredDepotList = new ArrayList<>();
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
					&& depotTransaction.getActionDate().before(depotRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateUntil()
					&& depotTransaction.getActionDate().after(depotRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != depotRequest.getFilter().getEventText() && !depotTransaction.getEventText().toUpperCase()
					.contains(depotRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredDepotList.add(depotTransaction);
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
		depotTransactionRequest.getDepotTransaction().setActionDate(new Date());

		// store depot
		DepotTransaction depotTransaction = createDepotSafe(depotTransactionRequest);

//		Account account = new Account(depotRequest.getDepotTransaction());
		
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
	private DepotTransaction createDepotSafe(DepotTransactionRequest depotCreateRequest) throws DamServiceException {
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
	public List<DepotTransaction> getDepotList() {
		List<DepotTransaction> depotTransaction = new ArrayList<>();
		Iterator<DepotTransaction> it = depotTransactionModel.findAll().iterator();
		while (it.hasNext()) {
			depotTransaction.add(it.next());
		}
		return depotTransaction;
	}

	private List<DepotTransaction> getDepotListByUserAction(Long userId, ActionType action) {
		return depotTransactionModel.findByUserAction(userId, action);
	}

	private List<DepotTransaction> getDepotListByUserDateFrom(Long userId, Date dateFrom) {
		return depotTransactionModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<DepotTransaction> getDepotListByUserDateUntil(Long userId, Date dateUntil) {
		return depotTransactionModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<DepotTransaction> getDepotListByUserActionDateFrom(Long userId, ActionType action, Date dateFrom) {
		return depotTransactionModel.findByUserActionDateFrom(userId, action, dateFrom);
	}

	private List<DepotTransaction> getDepotListByUserActionDateUntil(Long userId, ActionType action, Date dateUntil) {
		return depotTransactionModel.findByUserActionDateUntil(userId, action, dateUntil);
	}

	private List<DepotTransaction> getDepotListByUserActionDateFromDateUntil(Long userId, ActionType action, Date dateFrom,
			Date dateUntil) {
		return depotTransactionModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	private List<DepotTransaction> getDepotListByUser(Long userId) {
		return depotTransactionModel.findDepotByUser(userId);
	}

	public DepotTransaction getDepotById(Long depotId) {
		if (null == depotId) {
			return null;
		}

		Optional<DepotTransaction> optionalDepot = depotTransactionModel.findById(depotId);
		if (null != optionalDepot && optionalDepot.isPresent()) {
			return optionalDepot.get();
		}
		return null;
	}

	public Long dropDepot(DepotTransaction depotTransaction) {
		if (null != depotTransaction) {
			depotTransactionModel.deleteById(depotTransaction.getDepotTransactionId());
			DepotTransaction deletedDepot = getDepotById(depotTransaction.getDepotTransactionId());
			if (null == deletedDepot) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

}
