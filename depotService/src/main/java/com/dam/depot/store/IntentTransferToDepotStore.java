package com.dam.depot.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.AccountTransactionStore;
import com.dam.depot.BalanceStore;
import com.dam.depot.DepotStore;
import com.dam.depot.DepotTransactionStore;
import com.dam.depot.PermissionCheck;
import com.dam.depot.RequestHelper;
import com.dam.depot.model.IntentModel;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.model.entity.Balance;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.rest.message.intent.IntentUpdateRequest;
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
		
		Balance balance = balanceStore.getBalanceByUserId(storedIntent.getUserId());
		if (null == balance) {
			balance = new Balance();
			balance.setUserId(storedIntent.getUserId());
			balance.setAmountAccount(0f);
			balance.setAmountAccountIntent(0f);
			balance.setAmountDepot(0f);
			balance.setAmountDepotIntent(0f);
		}
		float amount = balance.getAmountDepotIntent() - storedIntent.getAmount();
		balance.setAmountDepotIntent(amount);
		balance.setLastUpdate(new Date());
		balance.setCurrency(storedIntent.getCurrency());
		
		balanceStore.saveBalance(balance);

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
		
		// 2. Insert into account
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setUserId(storedIntent.getUserId());
		accountTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		accountTransaction.setAction(ActionType.DEPOT_TRANSFER);
		accountTransaction.setActionDate(new Date());
		accountTransaction.setReferenceType(ReferenceType.INTENT);
		accountTransaction.setReferenceId(storedIntent.getIntentId());
		accountTransaction.setAmount(storedIntent.getAmount() *-1);
		accountTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		
		accountTransactionStore.storeAccountTransaction(accountTransaction);
		
		// 3. Update Balance for account
		Balance balance = balanceStore.getBalanceByUserId(storedIntent.getUserId());
		if (null == balance) {
			balance = new Balance();
			balance.setUserId(storedIntent.getUserId());
			balance.setAmountAccount(0f);
			balance.setAmountAccountIntent(0f);
			balance.setAmountDepot(0f);
			balance.setAmountDepotIntent(0f);
		}
		float amountAccount = balance.getAmountAccount() - storedIntent.getAmount();
		float amountDepotIntent = balance.getAmountAccount() - storedIntent.getAmount();
		float amountDepot = balance.getAmountDepot() + storedIntent.getAmount();
		balance.setAmountAccount(amountAccount);
		balance.setAmountDepotIntent(amountDepotIntent);
		balance.setAmountDepot(amountDepot);
		balance.setLastUpdate(new Date());
		
		balanceStore.saveBalance(balance);
		
		// 4. Insert into Depot Transaction
		DepotTransaction depotTransaction = new DepotTransaction();
		depotTransaction.setUserId(storedIntent.getUserId());
		depotTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		depotTransaction.setAction(ActionType.INVEST);
		depotTransaction.setActionDate(new Date());
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
		depot.setLastUpdate(new Date());
		
		depotStore.saveDepot(depot);
		
		return storedIntent;
		
	}

}
