package com.dam.depot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.model.AccountTransactionModel;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionListRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionRequest;
import com.dam.depot.rest.message.accountTransaction.AccountTransactionUpdateRequest;
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
public class AccountTransactionStore {

	@Autowired
	private AccountTransactionModel accountTransactionModel;

	public long count() {
		return accountTransactionModel.count();
	}

	/**
	 * Save getter for Account. request
	 * 
	 * @param account
	 * @return
	 */
	public AccountTransaction getAccountSafe(AccountTransactionRequest accountTransactionRequest) throws DamServiceException {
		if (null == accountTransactionRequest.getAccountTransaction().getAccountTransactionId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Account Transaction ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(accountTransactionRequest, accountTransactionRequest.getRequestorUserId(), accountTransactionRequest.getRights());
		PermissionCheck.isReadPermissionSet(accountTransactionRequest.getRequestorUserId(), null, accountTransactionRequest.getRights());

		// Read attemptions
		AccountTransaction accountTransaction = getAccountById(accountTransactionRequest.getAccountTransaction().getAccountTransactionId());
		if (null == accountTransaction) {
			throw new DamServiceException(new Long(404), "Account Unknown", "Account Transaction not found or invalid request");
		}

		return accountTransaction;
	}

	/**
	 * Delivers a list of account entries by filtering the params.
	 * 
	 * @param accountRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<AccountTransaction> getAccountListSafe(AccountTransactionListRequest accountRequest) throws DamServiceException {
		if (null == accountRequest.getAccountTransaction()) {
			throw new DamServiceException(500L, "Invalid request", "Account is not set in request.");
		}
		PermissionCheck.checkRequestedParams(accountRequest, accountRequest.getRequestorUserId(), accountRequest.getRights());
		PermissionCheck.isReadPermissionSet(accountRequest.getRequestorUserId(), null, accountRequest.getRights());

		List<AccountTransaction> accountList = null;
		// search by sql and indexed fields
		if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getAccountTransaction().getAction()
				&& null != accountRequest.getFilter().getDateFrom() && null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserActionDateFromDateUntil(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getAccountTransaction().getAction(), accountRequest.getFilter().getDateFrom(),
					accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getAccountTransaction().getAction()
				&& null != accountRequest.getFilter().getDateFrom()) {
			accountList = getAccountListByUserActionDateFrom(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getAccountTransaction().getAction(), accountRequest.getFilter().getDateFrom());
		} else if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getAccountTransaction().getAction()
				&& null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserActionDateUntil(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getAccountTransaction().getAction(), accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getFilter().getDateFrom()) {
			accountList = getAccountListByUserDateFrom(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getFilter().getDateFrom());
		} else if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserDateUntil(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccountTransaction().getUserId() && null != accountRequest.getAccountTransaction().getAction()) {
			accountList = getAccountListByUserAction(accountRequest.getAccountTransaction().getUserId(),
					accountRequest.getAccountTransaction().getAction());
		} else if (null != accountRequest.getAccountTransaction().getUserId()) {
			accountList = getAccountListByUser(accountRequest.getAccountTransaction().getUserId());
		}

		if (null == accountList) {
			throw new DamServiceException(500L, "No Account Entry found by request", "Request: " + accountRequest);
		}

		// Filter
		if (null == accountRequest.getFilter() || !accountRequest.getFilter().isFiltered()) {
			return accountList;
		}

		List<AccountTransaction> filteredAccountList = new ArrayList<>();
		Iterator<AccountTransaction> it = accountList.iterator();
		while (it.hasNext()) {
			AccountTransaction accountTransaction = it.next();
			if (null != accountRequest.getFilter().getAmountFrom()
					&& accountTransaction.getAmount() < accountRequest.getFilter().getAmountFrom()) {
				continue;
			}
			if (null != accountRequest.getFilter().getAmountUntil()
					&& accountTransaction.getAmount() > accountRequest.getFilter().getAmountUntil()) {
				continue;
			}
			if (null != accountRequest.getFilter().getDateFrom()
					&& accountTransaction.getActionDate().before(accountRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != accountRequest.getFilter().getDateUntil()
					&& accountTransaction.getActionDate().after(accountRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != accountRequest.getFilter().getEventText() && !accountTransaction.getEventText().toUpperCase()
					.contains(accountRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredAccountList.add(accountTransaction);
		}

		if (filteredAccountList.isEmpty()) {
			throw new DamServiceException(500L, "No Account Entry found by request", "Request: " + accountRequest);
		}

		return filteredAccountList;
	}

	/**
	 * Creation of account requests existing userId, givenName and lastName. userId in
	 * Entity Container mustn't be null
	 * 
	 * @param accountContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public AccountTransaction createAccountSafe(AccountTransactionRequest accountCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(accountCreateRequest, accountCreateRequest.getRequestorUserId(),
				accountCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(accountCreateRequest.getAccountTransaction(), AccountTransaction.class, "Account Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(accountCreateRequest.getRequestorUserId(), null,
				accountCreateRequest.getRights());

		if (null == accountCreateRequest.getAccountTransaction().getAction() || null == accountCreateRequest.getAccountTransaction().getActionDate()
				|| null == accountCreateRequest.getAccountTransaction().getAmount()
				|| null == accountCreateRequest.getAccountTransaction().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Account data not complete");
		}

		AccountTransaction accountTransaction;
		try {
			accountTransaction = accountTransactionModel.save(accountCreateRequest.getAccountTransaction());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Account could not be stored", e.getMessage());
		}

		if (null == accountTransaction) {
			throw new DamServiceException(new Long(422), "Account not created",
					"Account still exists, data invalid or not complete");
		}
		return accountTransaction;

	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public AccountTransaction updateAccountSafe(AccountTransactionUpdateRequest accountTransactionUpdateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(accountTransactionUpdateRequest, accountTransactionUpdateRequest.getRequestorUserId(),
				accountTransactionUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(accountTransactionUpdateRequest.getAccountTransaction(), AccountTransaction.class, "Account Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(accountTransactionUpdateRequest.getRequestorUserId(), null,
				accountTransactionUpdateRequest.getRights());

		// check if still exists
		AccountTransaction existingAccount = getAccountById(accountTransactionUpdateRequest.getAccountTransaction().getAccountTransactionId());

		// Account must exist and userId ist not permutable
		if (null == existingAccount) {
			throw new DamServiceException(new Long(404), "Account for update not found",
					"Account with accountId doesn't exist.");
		}

		return updateAccount(existingAccount, accountTransactionUpdateRequest.getAccountTransaction());
	}


	/**
	 * Lists all Accounts.
	 * 
	 * @return
	 */
	public List<AccountTransaction> getAccountList() {
		List<AccountTransaction> accountTransactions = new ArrayList<>();
		Iterator<AccountTransaction> it = accountTransactionModel.findAll().iterator();
		while (it.hasNext()) {
			accountTransactions.add(it.next());
		}
		return accountTransactions;
	}
	
	private List<AccountTransaction> getAccountListByUserAction(Long userId, ActionType action) {
		return accountTransactionModel.findByUserAction(userId, action);
	}

	private List<AccountTransaction> getAccountListByUserDateFrom(Long userId, Date dateFrom) {
		return accountTransactionModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<AccountTransaction> getAccountListByUserDateUntil(Long userId, Date dateUntil) {
		return accountTransactionModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<AccountTransaction> getAccountListByUserActionDateFrom(Long userId, ActionType action, Date dateFrom) {
		return accountTransactionModel.findByUserActionDateFrom(userId, action, dateFrom);
	}

	private List<AccountTransaction> getAccountListByUserActionDateUntil(Long userId, ActionType action, Date dateUntil) {
		return accountTransactionModel.findByUserActionDateUntil(userId, action, dateUntil);
	}

	private List<AccountTransaction> getAccountListByUserActionDateFromDateUntil(Long userId, ActionType action, Date dateFrom,
			Date dateUntil) {
		return accountTransactionModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	private List<AccountTransaction> getAccountListByUser(Long userId) {
		return accountTransactionModel.findAccountByUser(userId);
	}

	public AccountTransaction getAccountById(Long accountId) {
		if (null == accountId) {
			return null;
		}

		Optional<AccountTransaction> optionalAccount = accountTransactionModel.findById(accountId);
		if (null != optionalAccount && optionalAccount.isPresent()) {
			return optionalAccount.get();
		}
		return null;
	}

	/**
	 * Update account with changed values
	 * 
	 * @param accountForUpdate
	 * @param accountContainer
	 * @return
	 */
	private AccountTransaction updateAccount(AccountTransaction accountForUpdate, AccountTransaction accountContainer) throws DamServiceException {

		if (null != accountForUpdate && null != accountContainer) {
			accountForUpdate.updateEntity(accountContainer);
			try {
				return accountTransactionModel.save(accountForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "Account could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Account could not be saved", "Check Account data in request.");
	}
	
	public AccountTransaction storeAccountTransaction(AccountTransaction accountTransaction) throws DamServiceException {
		return accountTransactionModel.save(accountTransaction);
	}
}
