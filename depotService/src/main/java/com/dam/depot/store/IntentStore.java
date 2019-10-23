package com.dam.depot.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.PermissionCheck;
import com.dam.depot.RequestHelper;
import com.dam.depot.model.IntentModel;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.rest.message.intent.IntentUpdateRequest;
import com.dam.depot.types.ActionType;
import com.dam.depot.types.ReferenceType;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class IntentStore {

	@Autowired
	protected IntentModel intentModel;

	@Autowired
	protected AccountTransactionStore accountTransactionStore;

	@Autowired
	protected DepotTransactionStore depotTransactionStore;

	@Autowired
	protected AccountStatusStore accountStatusStore;

	@Autowired
	protected DepotStore depotStore;

	public long count() {
		return intentModel.count();
	}

	/**
	 * Deposit is stored to the Entity Intent and as value in Entity AccountStatus.
	 * 
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent storeIntentSafe(IntentRequest intentCreateRequest, ActionType action) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(action);
		RequestHelper.checkActions(intentCreateRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(intentCreateRequest.getIntent().getAmount());
		RequestHelper.checkCurrency(intentCreateRequest.getIntent().getCurrency());

		Intent intent = new Intent();
		intent.setIntent(intentCreateRequest.getIntent());
		intent.setBookingDate(null);
		intent.setActionDate(intentCreateRequest.getIntent().getActionDate());
		intent.setRequestorUserId(intentCreateRequest.getRequestorUserId());
		intent.setPortfolioId(intentCreateRequest.getIntent().getPortfolioId());
		intent = intentModel.save(intent);

		AccountStatus accountStatus = accountStatusStore.getAccountStatusByUserId(intentCreateRequest.getIntent().getUserId());
		if (null == accountStatus) {
			accountStatus = new AccountStatus();
			accountStatus.setUserId(intentCreateRequest.getIntent().getUserId());
			accountStatus.setAmountAccount(0f);
			accountStatus.setAmountAccountIntent(0f);
			accountStatus.setAmountDepot(0f);
			accountStatus.setAmountDepotIntent(0f);
		}
		switch (action) {
		case INVEST_INTENT:
		case TRANSFER_TO_DEPOT_INTENT:
			accountStatus.setAmountDepotIntent(accountStatus.getAmountDepotIntent() + intent.getAmount());
			break;

		case DEPOSIT_INTENT:
		case TRANSFER_TO_ACCOUNT_INTENT:
			accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() + intent.getAmount());
			break;

		case DEBIT_INTENT:
			accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() - intent.getAmount());
			break;

		case SELL_INTENT:
			accountStatus.setAmountDepotIntent(accountStatus.getAmountDepotIntent() - intent.getAmount());
			break;

		default:
			break;
		}
		accountStatus.setLastUpdate(new Date());
		accountStatus.setCurrency(intent.getCurrency());
		accountStatusStore.saveAccountStatus(accountStatus);

		return intent;
	}

	/**
	 * Called if intents are confirmed. Means that the house bank has confirmed. But
	 * not yet the depot bank! Regulary called by a cron scheduled system user.
	 * 
	 * @param confirmRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent confirmIntentSafe(IntentRequest confirmRequest, ActionType action) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(action);
		RequestHelper.checkActions(confirmRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(confirmRequest.getIntent().getAmount());
		RequestHelper.checkCurrency(confirmRequest.getIntent().getCurrency());

		Intent storedIntent = getIntentById(confirmRequest.getIntent().getIntentId());

		if (!storedIntent.getAmount().equals(confirmRequest.getIntent().getAmount())) {
			storedIntent.setFinishResponse("WARNING: confirmed amount [" + confirmRequest.getIntent().getAmount()
					+ "] is not equal to origin intent amount [" + storedIntent.getAmount() + "]; Finish Response was "
					+ confirmRequest.getIntent().getFinishResponse());
		}

		Intent updateIntent = new Intent();
		updateIntent.setIntent(confirmRequest.getIntent());
		updateIntent.setIntentId(confirmRequest.getIntent().getIntentId());
		updateIntent.setBooked(true);

		// For action check the requested intent had to be INVEST_INTENT_CONFIRMED
		// But in Database the ActionType mustn't change
		switch (action) {
		case INVEST_INTENT_CONFIRMED:
			updateIntent.setAction(ActionType.INVEST_INTENT);
			break;
		case DEPOSIT_INTENT_CONFIRMED:
			updateIntent.setAction(ActionType.DEPOSIT_INTENT);
			break;
		case DEBIT_INTENT_CONFIRMED:
			updateIntent.setAction(ActionType.DEBIT_INTENT);
			break;

		default:
			break;
		}

		// 1. Update with intent
		storedIntent = updateIntent(storedIntent, updateIntent);

		// 2. Insert into account transaction
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setUserId(storedIntent.getUserId());
		accountTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		accountTransaction.setActionDate(new Date());
		accountTransaction.setReferenceType(ReferenceType.INTENT);
		accountTransaction.setReferenceId(storedIntent.getIntentId());
		accountTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());

		switch (action) {
		case INVEST_INTENT_CONFIRMED:
		case DEPOSIT_INTENT_CONFIRMED:
			accountTransaction.setAction(ActionType.DEPOSIT);
			accountTransaction.setAmount(storedIntent.getAmount());
			break;
		case DEBIT_INTENT_CONFIRMED:
			accountTransaction.setAction(ActionType.DEBIT);
			accountTransaction.setAmount(storedIntent.getAmount() * -1);
			break;

		default:
			break;
		}

		accountTransactionStore.storeAccountTransaction(accountTransaction);

		// 3. Update AccountStatus for account
		AccountStatus accountStatus = accountStatusStore.getAccountStatusByUserId(confirmRequest.getIntent().getUserId());
		if (null == accountStatus) {
			accountStatus = new AccountStatus();
			accountStatus.setUserId(confirmRequest.getIntent().getUserId());
			accountStatus.setAmountAccount(0f);
			accountStatus.setAmountAccountIntent(0f);
			accountStatus.setAmountDepot(0f);
			accountStatus.setAmountDepotIntent(0f);
		}
		accountStatus.setLastUpdate(new Date());

		switch (action) {
		case INVEST_INTENT_CONFIRMED:
			accountStatus.setAmountAccount(accountStatus.getAmountAccount() + storedIntent.getAmount());
			break;

		case DEPOSIT_INTENT_CONFIRMED:
			accountStatus.setAmountAccount(accountStatus.getAmountAccount() + storedIntent.getAmount());
			accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() - storedIntent.getAmount());
			break;

		case DEBIT_INTENT_CONFIRMED:
			accountStatus.setAmountAccount(accountStatus.getAmountAccount() - Math.abs(storedIntent.getAmount()));
			accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() + Math.abs(storedIntent.getAmount()));
			break;

		default:
			break;
		}

		accountStatusStore.saveAccountStatus(accountStatus);

		// 4. Insert into Entity Intent
		if (action.equals(ActionType.INVEST_INTENT_CONFIRMED)) {
			updateIntent = new Intent();
			updateIntent.setIntent(storedIntent);
			updateIntent.setAccepted(false);
			updateIntent.setBooked(false);
			updateIntent.setBookingDate(null);
			updateIntent.setFinishResponse(null);
			updateIntent.setRequestorUserId(confirmRequest.getRequestorUserId());
			updateIntent.setReferenceId(storedIntent.getIntentId());
			updateIntent.setAction(ActionType.TRANSFER_TO_DEPOT_INTENT);

			updateIntent = intentModel.save(updateIntent);
		}

		return updateIntent;
	}

	/**
	 * If the house bank of the investor declines the deposit of the account, the
	 * investment cannot be performed.
	 * 
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineIntentSafe(IntentRequest declineRequest, ActionType action) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(action);
		RequestHelper.checkActions(declineRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(declineRequest.getIntent().getAmount());

		// 1. Intent
		Intent storedIntent = getIntentById(declineRequest.getIntent().getIntentId());

		Intent updateIntent = new Intent();
		updateIntent.setIntent(declineRequest.getIntent());
		updateIntent.setIntentId(declineRequest.getIntent().getIntentId());
		updateIntent.setBooked(true);

		// For action check the requested intent was set by calling user
		// But in Database the ActionType mustn't change
		switch (action) {
		case INVEST_INTENT_DECLINED:
			updateIntent.setAction(ActionType.INVEST_INTENT);
			break;

		case DEPOSIT_INTENT_DECLINED:
			updateIntent.setAction(ActionType.DEPOSIT_INTENT);
			break;

		case DEBIT_INTENT_DECLINED:
			updateIntent.setAction(ActionType.DEBIT_INTENT);
			break;

		case SELL_INTENT_DECLINED:
			updateIntent.setAction(ActionType.SELL_INTENT);
			break;

		default:
			break;
		}

		storedIntent = updateIntent(storedIntent, updateIntent);

		// 2. AccountStatus
		AccountStatus accountStatus = accountStatusStore.getAccountStatusByUserId(declineRequest.getIntent().getUserId());
		if (null == accountStatus) {
			accountStatus = new AccountStatus();
			accountStatus.setUserId(declineRequest.getIntent().getUserId());
			accountStatus.setAmountAccount(0f);
			accountStatus.setAmountAccountIntent(0f);
			accountStatus.setAmountDepot(0f);
			accountStatus.setAmountDepotIntent(0f);
		}
		accountStatus.setLastUpdate(new Date());
		accountStatus.setCurrency(storedIntent.getCurrency());

		switch (action) {
		case INVEST_INTENT_DECLINED:
			accountStatus.setAmountDepotIntent(accountStatus.getAmountDepotIntent() - storedIntent.getAmount());
			break;

		case DEPOSIT_INTENT_DECLINED:
		case DEBIT_INTENT_DECLINED:
			accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() - storedIntent.getAmount());
			break;

		case SELL_INTENT_DECLINED:
			accountStatus.setAmountDepotIntent(accountStatus.getAmountDepotIntent() + storedIntent.getAmount());
			break;

		default:
			break;
		}

		accountStatusStore.saveAccountStatus(accountStatus);

		return storedIntent;
	}

	/**
	 * Save getter for Intent. request
	 * 
	 * @param intent
	 * @return
	 */
	public Intent getIntentSafe(IntentRequest intentRequest) throws DamServiceException {
		if (null == intentRequest.getIntent().getIntentId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Intent ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(intentRequest, intentRequest.getRequestorUserId(),
				intentRequest.getRights());
		PermissionCheck.isReadPermissionSet(intentRequest.getRequestorUserId(), null, intentRequest.getRights());

		Intent intent = getIntentById(intentRequest.getIntent().getIntentId());
		if (null == intent) {
			throw new DamServiceException(new Long(404), "Intent Unknown", "Intent not found or invalid request");
		}

		return intent;
	}

	/**
	 * Delivers a list of intent entries by filtering the params.
	 * 
	 * @param intentRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<Intent> getIntentListSafe(IntentRequest intentRequest) throws DamServiceException {
		if (null == intentRequest.getIntent()) {
			return null;
		}
		PermissionCheck.checkRequestedParams(intentRequest, intentRequest.getRequestorUserId(),
				intentRequest.getRights());
		PermissionCheck.isReadPermissionSet(intentRequest.getRequestorUserId(), null, intentRequest.getRights());

		List<Intent> listIntent = getIntentListByActionBooked(intentRequest.getIntent().getAction(),
				intentRequest.getIntent().getBooked());

		return listIntent;
	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public Intent updateIntentSafe(IntentUpdateRequest intentUpdateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(intentUpdateRequest, intentUpdateRequest.getRequestorUserId(),
				intentUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(intentUpdateRequest.getIntent(), Intent.class, "Intent Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(intentUpdateRequest.getRequestorUserId(), null,
				intentUpdateRequest.getRights());

		// check if still exists
		Intent existingIntent = getIntentById(intentUpdateRequest.getIntent().getIntentId());

		// Intent must exist and userId ist not permutable
		if (null == existingIntent) {
			throw new DamServiceException(new Long(404), "Intent for update not found",
					"Intent with intentId doesn't exist.");
		}

		return updateIntent(existingIntent, intentUpdateRequest.getIntent());
	}

	/**
	 * Lists all Intents.
	 * 
	 * @return
	 */
	public List<Intent> getIntentList() {
		List<Intent> intents = new ArrayList<>();
		Iterator<Intent> it = intentModel.findAll().iterator();
		while (it.hasNext()) {
			intents.add(it.next());
		}
		return intents;
	}
	
	public List<Intent> getIntentOpenListByUser(Long userId) {
		return intentModel.findByUserNotBooked(userId);
	}

	private List<Intent> getIntentListByActionBooked(ActionType action, Boolean booked) {
		if (booked) {
			return intentModel.findByActionBooked(action);
		} else {
			return intentModel.findByActionNotBooked(action);
		}

	}

	protected List<Intent> getIntentListByUserActionBooked(Long userId, ActionType action, Boolean booked) {
		if (booked) {
			return intentModel.findByUserActionBooked(userId, action);
		} else {
			return intentModel.findByUserActionNotBooked(userId, action);
		}
	}

	protected Intent getIntentById(Long intentId) {
		if (null == intentId) {
			return null;
		}

		Optional<Intent> optionalIntent = intentModel.findById(intentId);
		if (null != optionalIntent && optionalIntent.isPresent()) {
			return optionalIntent.get();
		}
		return null;
	}

	/**
	 * Update intent with changed values
	 * 
	 * @param intentForUpdate
	 * @param intentContainer
	 * @return
	 */
	protected Intent updateIntent(Intent intentForUpdate, Intent intentContainer) throws DamServiceException {

		if (null != intentForUpdate && null != intentContainer) {
			intentForUpdate.updateEntity(intentContainer);
			try {
				return intentModel.save(intentForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "Intent could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Intent could not be saved", "Check Intent data in request.");
	}
}
