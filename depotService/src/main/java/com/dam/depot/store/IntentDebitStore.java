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
public class IntentDebitStore extends IntentStore {

	/**
	 * Debit is stored to the Entity Intent and as value in Entity AccountStatus.
	 * @return
	 * @throws DamServiceException
	 */
	public Intent debitIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		return storeIntentSafe(intentCreateRequest, ActionType.DEBIT_INTENT);
	}
	
	public Intent confirmDebitSafe(IntentRequest confirmRequest) throws DamServiceException {
		return confirmIntentSafe(confirmRequest, ActionType.DEBIT_INTENT_CONFIRMED);
	}
	
	/**
	 * If the house bank of the investor declines the debit of the account, the transfer cannot be performed.
	 * @param declineRequest
	 * @return
	 * @throws DamServiceException
	 */
	public Intent declineDebitSafe(IntentRequest declineRequest) throws DamServiceException {
		return declineIntentSafe(declineRequest, ActionType.DEBIT_INTENT_DECLINED);
	}
	
}
