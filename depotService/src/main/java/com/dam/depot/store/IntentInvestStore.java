package com.dam.depot.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.dam.depot.RequestHelper;
import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.model.entity.Balance;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentRequest;
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
public class IntentInvestStore extends IntentStore {

	/**
	 * Deposit is only stored to the Entity Intent and as value in Entity Balance.
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent investIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		RequestHelper.checkPortfolio(intentCreateRequest.getIntent().getPortfolioId());
		return storeIntentSafe(intentCreateRequest, ActionType.INVEST_INTENT);
	}
	
	/**
	 * If the house bank of the investor declines the deposit of the account, the investment cannot be performed.
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineInvestSafe(IntentRequest declineRequest) throws DamServiceException {
		return declineIntentSafe(declineRequest, ActionType.INVEST_INTENT_DECLINED);
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
		RequestHelper.checkPortfolio(confirmRequest.getIntent().getPortfolioId());
		return confirmIntentSafe(confirmRequest, ActionType.INVEST_INTENT_CONFIRMED);
	}
}
