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
import com.dam.depot.PermissionCheck;
import com.dam.depot.RequestHelper;
import com.dam.depot.model.IntentModel;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.model.entity.Balance;
import com.dam.depot.model.entity.Depot;
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
public class IntentInvestStore extends IntentStore {

	/**
	 * Deposit is only stored to the Entity Intent and as value in Entity Balance.
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent investIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.INVEST_INTENT);
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
		
		Balance balance = balanceStore.getBalanceByUserId(intentCreateRequest.getIntent().getUserId());
		if (null == balance) {
			balance = new Balance();
			balance.setUserId(intentCreateRequest.getIntent().getUserId());
			balance.setAmountAccount(0f);
			balance.setAmountAccountIntent(0f);
			balance.setAmountDepot(0f);
			balance.setAmountDepotIntent(0f);
		}
		float amount = balance.getAmountDepotIntent() + intent.getAmount();
		balance.setAmountDepotIntent(amount);
		balance.setLastUpdate(new Date());
		balance.setCurrency(intent.getCurrency());
		balanceStore.saveBalance(balance);

		return intent;
	}
	
	/**
	 * If the house bank of the investor declines the deposit of the account, the investment cannot be performed.
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineInvestSafe(IntentRequest declineRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.INVEST_INTENT_DECLINED);
		RequestHelper.checkActions(declineRequest.getIntent().getAction(), allowedActions);
		RequestHelper.checkAmountTransfer(declineRequest.getIntent().getAmount());
		
		Intent storedIntent = getIntentById(declineRequest.getIntent().getIntentId());
		
		Intent updateIntent = new Intent();
		updateIntent.setIntent(declineRequest.getIntent());
		updateIntent.setIntentId(declineRequest.getIntent().getIntentId());
		updateIntent.setBooked(true);
		
		// For action check the requested intent had to be INVEST_INTENT_CONFIRMED
		// But in Database the ActionType mustn't change
		updateIntent.setAction(ActionType.INVEST_INTENT);
		
		storedIntent = updateIntent(storedIntent, updateIntent);
		
		Balance balance = balanceStore.getBalanceByUserId(declineRequest.getIntent().getUserId());
		if (null == balance) {
			balance = new Balance();
			balance.setUserId(declineRequest.getIntent().getUserId());
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
	 * Means that the house bank has confirmed. But not yet the depot bank!
	 * Regulary called by a cron scheduled system user.
	 * @param confirmRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent confirmInvestSafe(IntentRequest confirmRequest) throws DamServiceException {
		List<ActionType> allowedActions = new ArrayList<>();
		allowedActions.add(ActionType.INVEST_INTENT_CONFIRMED);
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
		updateIntent.setAction(ActionType.INVEST_INTENT);
		
		// 1. Update with intent
		storedIntent = updateIntent(storedIntent, updateIntent);
		
		// 2. Insert into account
		AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setUserId(storedIntent.getUserId());
		accountTransaction.setRequestorUserId(storedIntent.getRequestorUserId());
		accountTransaction.setAction(ActionType.DEPOSIT);
		accountTransaction.setActionDate(new Date());
		accountTransaction.setReferenceType(ReferenceType.INTENT);
		accountTransaction.setReferenceId(storedIntent.getIntentId());
		accountTransaction.setAmount(storedIntent.getAmount());
		accountTransaction.setRequestorUserId(confirmRequest.getRequestorUserId());
		
		accountTransactionStore.storeAccountTransaction(accountTransaction);
		
		// 3. Update Balance for account
		Balance balance = balanceStore.getBalanceByUserId(confirmRequest.getIntent().getUserId());
		if (null == balance) {
			balance = new Balance();
			balance.setUserId(confirmRequest.getIntent().getUserId());
			balance.setAmountAccount(0f);
			balance.setAmountAccountIntent(0f);
			balance.setAmountDepot(0f);
			balance.setAmountDepotIntent(0f);
		}
		float amount = balance.getAmountAccount() + storedIntent.getAmount();
		balance.setAmountAccount(amount);
		balance.setLastUpdate(new Date());
		
		balanceStore.saveBalance(balance);
		
		// 4. Insert into Entity Intent
		Intent newIntent = new Intent();
		newIntent.setIntent(storedIntent);
		newIntent.setAction(ActionType.TRANSFER_TO_DEPOT_INTENT);
		newIntent.setAccepted(false);
		newIntent.setBooked(false);
		newIntent.setBookingDate(null);
		newIntent.setFinishResponse(null);
		newIntent.setRequestorUserId(confirmRequest.getRequestorUserId());
		newIntent.setReferenceId(storedIntent.getIntentId());

		return intentModel.save(newIntent);
	}
}
