package com.dam.depot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.model.IntentModel;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.intent.IntentListRequest;
import com.dam.depot.rest.message.intent.IntentRequest;
import com.dam.depot.rest.message.intent.IntentUpdateRequest;
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
public class IntentStore {

	@Autowired
	private IntentModel intentModel;

	@Autowired

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
	public List<Intent> getIntentListSafe(IntentListRequest intentRequest) throws DamServiceException {
		if (null == intentRequest.getIntent()) {
			throw new DamServiceException(500L, "Invalid request", "Intent is not set in request.");
		}
		PermissionCheck.checkRequestedParams(intentRequest, intentRequest.getRequestorUserId(),
				intentRequest.getRights());
		PermissionCheck.isReadPermissionSet(intentRequest.getRequestorUserId(), null, intentRequest.getRights());

		boolean booked = null == intentRequest.getIntent().getBookingDate() ? false : true;
		List<Intent> listIntent = getIntentListByUserActionBooked(intentRequest.getIntent().getUserId(),
				intentRequest.getIntent().getAction(), booked);

		return listIntent;
	}

	/**
	 * Creation of intent requests existing userId, givenName and lastName. userId
	 * in Entity Container mustn't be null
	 * 
	 * @param intentContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public Intent createIntentSafe(IntentRequest intentCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(intentCreateRequest, intentCreateRequest.getRequestorUserId(),
				intentCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(intentCreateRequest.getIntent(), Intent.class, "Intent Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(intentCreateRequest.getRequestorUserId(), null,
				intentCreateRequest.getRights());

		if (null == intentCreateRequest.getIntent().getAction()
				|| null == intentCreateRequest.getIntent().getActionDate()
				|| null == intentCreateRequest.getIntent().getAmount()
				|| null == intentCreateRequest.getRequestorUserId()
				|| null == intentCreateRequest.getIntent().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Intent data not complete");
		}

		Intent intent;
		try {
			intent = intentModel.save(intentCreateRequest.getIntent());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Intent could not be stored", e.getMessage());
		}

		if (null == intent) {
			throw new DamServiceException(new Long(422), "Intent not created",
					"Intent still exists, data invalid or not complete");
		}
		return intent;
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

	private List<Intent> getIntentListByUserActionBooked(Long userId, ActionType action, Boolean booked) {
		return intentModel.findByUserActionBooked(userId, action, booked);
	}

	private Intent getIntentById(Long intentId) {
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
	private Intent updateIntent(Intent intentForUpdate, Intent intentContainer) throws DamServiceException {

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
