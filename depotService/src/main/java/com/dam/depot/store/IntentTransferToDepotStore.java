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
public class IntentTransferToDepotStore extends IntentStore {
	
	public Intent transferToDepotIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		RequestHelper.checkPortfolio(intentCreateRequest.getIntent().getPortfolioId());
		return storeIntentSafe(intentCreateRequest, ActionType.TRANSFER_TO_DEPOT_INTENT);
	}

	/**
	 * If the house bank of the investor declines the deposit of the account, the investment cannot be performed.
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineTransferToDepotSafe(IntentRequest declineRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.TRANSFER_TO_DEPOT_DECLINED);
		RequestHelper.checkActions(declineRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(declineRequest.getIntent().getAmount());
		
		Intent storedIntent = getIntentById(declineRequest.getIntent().getIntentId());
		
		Intent updateIntent = new Intent();
		updateIntent.setIntent(declineRequest.getIntent());
		updateIntent.setIntentId(declineRequest.getIntent().getIntentId());
		updateIntent.setBooked(true);
		
		// For action check the requested intent had to be INVEST_INTENT_CONFIRMED
		// But in Database the ActionType mustn't change
		updateIntent.setAction(ActionType.TRANSFER_TO_DEPOT_INTENT);
		
		storedIntent = updateIntent(storedIntent, updateIntent);
		
		AccountStatus accountStatus = accountStatusStore.getAccountStatusByUserId(storedIntent.getUserId());
		if (null == accountStatus) {
			accountStatus = new AccountStatus();
			accountStatus.setUserId(storedIntent.getUserId());
			accountStatus.setAmountAccount(0f);
			accountStatus.setAmountAccountIntent(0f);
			accountStatus.setAmountInvest(0f);
			accountStatus.setAmountDepotIntent(0f);
		}
		float amount = accountStatus.getAmountDepotIntent() - storedIntent.getAmount();
		accountStatus.setAmountDepotIntent(amount);
		accountStatus.setLastUpdate(LocalDateTime.now());
		accountStatus.setCurrency(storedIntent.getCurrency());
		
		accountStatusStore.saveAccountStatus(accountStatus);

		return storedIntent;
	}
	
	/**
	 * Called if invest intents are confirmed.
	 * Regulary called by a cron scheduled system user.
	 * @param confirmRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent confirmTransferToDepotSafe(IntentRequest confirmRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.TRANSFER_TO_DEPOT_CONFIRMED);
		RequestHelper.checkActions(confirmRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(confirmRequest.getIntent().getAmount());
		RequestHelper.checkCurrency(confirmRequest.getIntent().getCurrency());
		RequestHelper.checkPortfolio(confirmRequest.getIntent().getPortfolioId());

		Intent storedIntent = getIntentById(confirmRequest.getIntent().getIntentId());
		
		if (!storedIntent.getAmount().equals(confirmRequest.getIntent().getAmount())) {
			storedIntent.setFinishResponse("WARNING: confirmed amount [" + confirmRequest.getIntent().getAmount() + "] is not equal to origin intent amount [" + storedIntent.getAmount() +"]; Finish Response was " + confirmRequest.getIntent().getFinishResponse());
		}
		
		Intent updateIntent = new Intent();
		updateIntent.setIntent(confirmRequest.getIntent());
		updateIntent.setIntentId(confirmRequest.getIntent().getIntentId());
		updateIntent.setBooked(true);
		
		// For action check the requested intent had to be INVEST_INTENT_CONFIRMED
		// But in Database the ActionType mustn't change
		updateIntent.setAction(ActionType.TRANSFER_TO_DEPOT_INTENT);
		
		// 1. Update with intent
		storedIntent = updateIntent(storedIntent, updateIntent);
		
		// 2. Insert into account transaction
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setUserId(storedIntent.getUserId());
		accountTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		accountTransaction.setAction(ActionType.DEPOT_TRANSFER);
		accountTransaction.setActionDate(LocalDateTime.now());
		accountTransaction.setReferenceType(ReferenceType.INTENT);
		accountTransaction.setReferenceId(storedIntent.getIntentId());
		accountTransaction.setAmount(storedIntent.getAmount() *-1);
		accountTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		
		accountTransactionStore.storeAccountTransaction(accountTransaction);
		
		// 3. Update account status for account
		AccountStatus accountStatus = accountStatusStore.getAccountStatusByUserId(storedIntent.getUserId());
		if (null == accountStatus) {
			accountStatus = new AccountStatus();
			accountStatus.setUserId(storedIntent.getUserId());
			accountStatus.setAmountAccount(0f);
			accountStatus.setAmountAccountIntent(0f);
			accountStatus.setAmountInvest(0f);
			accountStatus.setAmountDepotIntent(0f);
		}
		float amountAccount = accountStatus.getAmountAccount() - storedIntent.getAmount();
		float amountDepotIntent = accountStatus.getAmountDepotIntent() - storedIntent.getAmount();
		float amountDepot = accountStatus.getAmountInvest() + storedIntent.getAmount();
		accountStatus.setAmountAccount(amountAccount);
		accountStatus.setAmountDepotIntent(amountDepotIntent);
		accountStatus.setAmountInvest(amountDepot);
		accountStatus.setLastUpdate(LocalDateTime.now());
		
		accountStatusStore.saveAccountStatus(accountStatus);
		
		// 4. Insert into Depot Transaction
		DepotTransaction depotTransaction = new DepotTransaction();
		depotTransaction.setUserId(storedIntent.getUserId());
		depotTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		depotTransaction.setAction(ActionType.DEPOT_TRANSFER);
		depotTransaction.setActionDate(LocalDateTime.now());
		depotTransaction.setReferenceType(ReferenceType.INTENT);
		depotTransaction.setReferenceId(storedIntent.getIntentId());
		depotTransaction.setAmount(storedIntent.getAmount());
		depotTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		depotTransaction.setPortfolioId(storedIntent.getPortfolioId());
		
		depotTransactionStore.storeDepotTransaction(depotTransaction);
		
		// 5. Update Entity Depot
		Depot depot = depotStore.getDepotByUserPortfolio(storedIntent.getUserId(), storedIntent.getPortfolioId());
		if (null == depot) {
			depot = new Depot();
			depot.setUserId(storedIntent.getUserId());
			depot.setPortfolioId(storedIntent.getPortfolioId());
			depot.setInvestValue(0f);
			depot.setCurrency(Currency.EUR);
		}
		float investValue = depot.getInvestValue() + storedIntent.getAmount();
		depot.setInvestValue(investValue);
		depot.setLastUpdate(LocalDateTime.now());
		
		depotStore.saveDepot(depot);
		
		return storedIntent;
	}
}
