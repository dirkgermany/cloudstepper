package com.dam.depot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.model.AccountModel;
import com.dam.depot.model.entity.Account;
import com.dam.depot.rest.message.account.AccountCreateRequest;
import com.dam.depot.rest.message.account.AccountListRequest;
import com.dam.depot.rest.message.account.AccountRequest;
import com.dam.depot.rest.message.account.AccountUpdateRequest;
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
public class AccountStore {

	@Autowired
	private AccountModel accountModel;

	@Autowired

	public long count() {
		return accountModel.count();
	}

	/**
	 * Save getter for Account. request
	 * 
	 * @param account
	 * @return
	 */
	public Account getAccountSafe(AccountRequest accountRequest) throws DamServiceException {
		if (null == accountRequest.getAccount().getAccountId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Account ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(accountRequest, accountRequest.getRequestorUserId(), accountRequest.getRights());
		PermissionCheck.isReadPermissionSet(accountRequest.getRequestorUserId(), null, accountRequest.getRights());

		// Read attemptions
		Account account = getAccountById(accountRequest.getAccount().getAccountId());
		if (null == account) {
			throw new DamServiceException(new Long(404), "Account Unknown", "Account not found or invalid request");
		}

		return account;
	}

	/**
	 * Delivers a list of account entries by filtering the params.
	 * 
	 * @param accountRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<Account> getAccountListSafe(AccountListRequest accountRequest) throws DamServiceException {
		if (null == accountRequest.getAccount()) {
			throw new DamServiceException(500L, "Invalid request", "Account is not set in request.");
		}
		PermissionCheck.checkRequestedParams(accountRequest, accountRequest.getRequestorUserId(), accountRequest.getRights());
		PermissionCheck.isReadPermissionSet(accountRequest.getRequestorUserId(), null, accountRequest.getRights());

		List<Account> accountList = null;
		// search by sql and indexed fields
		if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getAccount().getAction()
				&& null != accountRequest.getFilter().getDateFrom() && null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserActionDateFromDateUntil(accountRequest.getAccount().getUserId(),
					accountRequest.getAccount().getAction(), accountRequest.getFilter().getDateFrom(),
					accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getAccount().getAction()
				&& null != accountRequest.getFilter().getDateFrom()) {
			accountList = getAccountListByUserActionDateFrom(accountRequest.getAccount().getUserId(),
					accountRequest.getAccount().getAction(), accountRequest.getFilter().getDateFrom());
		} else if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getAccount().getAction()
				&& null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserActionDateUntil(accountRequest.getAccount().getUserId(),
					accountRequest.getAccount().getAction(), accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getFilter().getDateFrom()) {
			accountList = getAccountListByUserDateFrom(accountRequest.getAccount().getUserId(),
					accountRequest.getFilter().getDateFrom());
		} else if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getFilter().getDateUntil()) {
			accountList = getAccountListByUserDateUntil(accountRequest.getAccount().getUserId(),
					accountRequest.getFilter().getDateUntil());
		} else if (null != accountRequest.getAccount().getUserId() && null != accountRequest.getAccount().getAction()) {
			accountList = getAccountListByUserAction(accountRequest.getAccount().getUserId(),
					accountRequest.getAccount().getAction());
		} else if (null != accountRequest.getAccount().getUserId()) {
			accountList = getAccountListByUser(accountRequest.getAccount().getUserId());
		}

		if (null == accountList) {
			throw new DamServiceException(500L, "No Account Entry found by request", "Request: " + accountRequest);
		}

		// Filter
		if (null == accountRequest.getFilter() || !accountRequest.getFilter().isFiltered()) {
			return accountList;
		}

		List<Account> filteredAccountList = new ArrayList<>();
		Iterator<Account> it = accountList.iterator();
		while (it.hasNext()) {
			Account account = it.next();
			if (null != accountRequest.getFilter().getAmountFrom()
					&& account.getAmount() < accountRequest.getFilter().getAmountFrom()) {
				continue;
			}
			if (null != accountRequest.getFilter().getAmountUntil()
					&& account.getAmount() > accountRequest.getFilter().getAmountUntil()) {
				continue;
			}
			if (null != accountRequest.getFilter().getDateFrom()
					&& account.getActionDate().before(accountRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != accountRequest.getFilter().getDateUntil()
					&& account.getActionDate().after(accountRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != accountRequest.getFilter().getEventText() && !account.getEventText().toUpperCase()
					.contains(accountRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredAccountList.add(account);
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
	public Account createAccountSafe(AccountRequest accountCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(accountCreateRequest, accountCreateRequest.getRequestorUserId(),
				accountCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(accountCreateRequest.getAccount(), Account.class, "Account Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(accountCreateRequest.getRequestorUserId(), null,
				accountCreateRequest.getRights());

		if (null == accountCreateRequest.getAccount().getAction() || null == accountCreateRequest.getAccount().getActionDate()
				|| null == accountCreateRequest.getAccount().getAmount()
				|| null == accountCreateRequest.getAccount().getRequestorUserId()
				|| null == accountCreateRequest.getAccount().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Account data not complete");
		}

		Account account;
		try {
			account = accountModel.save(accountCreateRequest.getAccount());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Account could not be stored", e.getMessage());
		}

		if (null == account) {
			throw new DamServiceException(new Long(422), "Account not created",
					"Account still exists, data invalid or not complete");
		}
		return account;

	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public Account updateAccountSafe(AccountUpdateRequest accountUpdateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(accountUpdateRequest, accountUpdateRequest.getRequestorUserId(),
				accountUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(accountUpdateRequest.getAccount(), Account.class, "Account Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(accountUpdateRequest.getRequestorUserId(), null,
				accountUpdateRequest.getRights());

		// check if still exists
		Account existingAccount = getAccountById(accountUpdateRequest.getAccount().getAccountId());

		// Account must exist and userId ist not permutable
		if (null == existingAccount) {
			throw new DamServiceException(new Long(404), "Account for update not found",
					"Account with accountId doesn't exist.");
		}

		return updateAccount(existingAccount, accountUpdateRequest.getAccount());
	}


	/**
	 * Lists all Accounts.
	 * 
	 * @return
	 */
	public List<Account> getAccountList() {
		List<Account> accounts = new ArrayList<>();
		Iterator<Account> it = accountModel.findAll().iterator();
		while (it.hasNext()) {
			accounts.add(it.next());
		}
		return accounts;
	}
	
	private List<Account> getAccountListByUserAction(Long userId, ActionType action) {
		return accountModel.findByUserAction(userId, action);
	}

	private List<Account> getAccountListByUserDateFrom(Long userId, Date dateFrom) {
		return accountModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<Account> getAccountListByUserDateUntil(Long userId, Date dateUntil) {
		return accountModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<Account> getAccountListByUserActionDateFrom(Long userId, ActionType action, Date dateFrom) {
		return accountModel.findByUserActionDateFrom(userId, action, dateFrom);
	}

	private List<Account> getAccountListByUserActionDateUntil(Long userId, ActionType action, Date dateUntil) {
		return accountModel.findByUserActionDateUntil(userId, action, dateUntil);
	}

	private List<Account> getAccountListByUserActionDateFromDateUntil(Long userId, ActionType action, Date dateFrom,
			Date dateUntil) {
		return accountModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	private List<Account> getAccountListByUser(Long userId) {
		return accountModel.findAccountByUser(userId);
	}

	public Account getAccountById(Long accountId) {
		if (null == accountId) {
			return null;
		}

		Optional<Account> optionalAccount = accountModel.findById(accountId);
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
	private Account updateAccount(Account accountForUpdate, Account accountContainer) throws DamServiceException {

		if (null != accountForUpdate && null != accountContainer) {
			accountForUpdate.updateEntity(accountContainer);
			try {
				return accountModel.save(accountForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "Account could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Account could not be saved", "Check Account data in request.");
	}
}
