package com.dam.depot.store;

import org.springframework.stereotype.Controller;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.types.ActionType;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class IntentDepositStore extends IntentStore {

	/**
	 * Deposit is stored to the Entity Intent and as value in Entity AccountStatus.
	 * @param depotDepositRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent depositIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		return storeIntentSafe(intentCreateRequest, ActionType.DEPOSIT_INTENT);
	}
	
	public Intent confirmDepositSafe(IntentRequest confirmRequest) throws DamServiceException {
		return confirmIntentSafe(confirmRequest, ActionType.DEPOSIT_INTENT_CONFIRMED);
	}
	
	/**
	 * If the house bank of the investor declines the deposit of the account, the transfer cannot be performed.
	 * @param delineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineDepositSafe(IntentRequest declineRequest) throws DamServiceException {
		return declineIntentSafe(declineRequest, ActionType.DEPOSIT_INTENT_DECLINED);
	}
	
}
