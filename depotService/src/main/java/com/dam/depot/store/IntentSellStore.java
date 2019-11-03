package com.dam.depot.store;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import com.dam.depot.RequestHelper;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.types.ActionType;
import com.dam.depot.types.Currency;
import com.dam.depot.types.ReferenceType;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class IntentSellStore extends IntentStore {

	/**
	 * Deposit is only stored to the Entity Intent and as value in Entity AccountStatus.
	 * 
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent sellIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		RequestHelper.checkPortfolio(intentCreateRequest.getIntent().getPortfolioId());
		return storeIntentSafe(intentCreateRequest, ActionType.SELL_INTENT);
	}

	/**
	 * If the house bank of the investor declines the deposit of the account, the
	 * investment cannot be performed.
	 * 
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineSellSafe(IntentRequest declineRequest) throws DamServiceException {
		return declineIntentSafe(declineRequest, ActionType.SELL_INTENT_DECLINED);
	}

	/**
	 * Called if invest intents are confirmed. Means that the house bank has
	 * confirmed. But not yet the depot bank! Regulary called by a cron scheduled
	 * system user.
	 * 
	 * @param confirmRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent confirmSellSafe(IntentRequest confirmRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.SELL_INTENT_CONFIRMED);
		RequestHelper.checkActions(confirmRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(confirmRequest.getIntent().getAmount());
		RequestHelper.checkCurrency(confirmRequest.getIntent().getCurrency());
		RequestHelper.checkPortfolio(confirmRequest.getIntent().getPortfolioId());

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
		updateIntent.setAction(ActionType.SELL_INTENT);

		// 1. Update with intent
		storedIntent = updateIntent(storedIntent, updateIntent);

		// 2. Insert into account transaction
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setUserId(storedIntent.getUserId());
		accountTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		accountTransaction.setActionDate(LocalDateTime.now());
		accountTransaction.setReferenceType(ReferenceType.INTENT);
		accountTransaction.setReferenceId(storedIntent.getIntentId());
		accountTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		accountTransaction.setAction(ActionType.SELL);
		accountTransaction.setAmount(storedIntent.getAmount());

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
		accountStatus.setLastUpdate(LocalDateTime.now());
		accountStatus.setAmountDepot(accountStatus.getAmountDepot() - Math.abs(storedIntent.getAmount()));
		accountStatus.setAmountAccount(accountStatus.getAmountAccount() + Math.abs(storedIntent.getAmount()));
		accountStatus.setAmountAccountIntent(accountStatus.getAmountAccountIntent() - Math.abs(storedIntent.getAmount()));
		accountStatus.setAmountDepotIntent(accountStatus.getAmountDepotIntent() + Math.abs(storedIntent.getAmount()));

		accountStatusStore.saveAccountStatus(accountStatus);

		// 5. Insert auf depot_transaction
		DepotTransaction depotTransaction = new DepotTransaction();
		depotTransaction.setUserId(storedIntent.getUserId());
		depotTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		depotTransaction.setActionDate(LocalDateTime.now());
		depotTransaction.setReferenceType(ReferenceType.INTENT);
		depotTransaction.setReferenceId(storedIntent.getIntentId());
		depotTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		depotTransaction.setPortfolioId(storedIntent.getPortfolioId());
		depotTransaction.setAction(ActionType.SELL);
		depotTransaction.setAmount(storedIntent.getAmount() * -1);

		depotTransactionStore.storeDepotTransaction(depotTransaction);

		// 6. Update auf depot
		Depot depot = depotStore.getDepotByUserPortfolio(storedIntent.getUserId(), storedIntent.getPortfolioId());
		if (null == depot) {
			depot = new Depot();
			depot.setUserId(storedIntent.getUserId());
			depot.setPortfolioId(storedIntent.getPortfolioId());
			depot.setInvestValue(0f);
			depot.setCurrency(Currency.EUR);
		}
		float investValue = depot.getInvestValue() - storedIntent.getAmount();
		depot.setInvestValue(investValue);
		depot.setLastUpdate(LocalDateTime.now());

		depotStore.saveDepot(depot);

		// 4. Insert into Entity Intent
		updateIntent = new Intent();
		updateIntent.setIntent(storedIntent);
		updateIntent.setAccepted(false);
		updateIntent.setBooked(false);
		updateIntent.setBookingDate(null);
		updateIntent.setFinishResponse(null);
		updateIntent.setRequestorUserId(confirmRequest.getRequestorUserId());
		updateIntent.setReferenceId(storedIntent.getIntentId());
		updateIntent.setAction(ActionType.DEBIT_INTENT);

		updateIntent = intentModel.save(updateIntent);

		return updateIntent;
	}
}
