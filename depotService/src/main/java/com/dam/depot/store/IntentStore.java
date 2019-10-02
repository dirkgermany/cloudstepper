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
public class IntentStore {

	@Autowired
	protected IntentModel intentModel;

	@Autowired
	protected AccountTransactionStore accountTransactionStore;

	@Autowired
	protected DepotTransactionStore depotTransactionStore;

	@Autowired
	protected BalanceStore balanceStore;

	@Autowired
	protected DepotStore depotStore;

	public long count() {
		return intentModel.count();
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
