package com.dam.coach.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.coach.PermissionCheck;
import com.dam.coach.model.CoachActionModel;
import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.coachAction.CoachActionCreateRequest;
import com.dam.coach.rest.message.coachAction.CoachActionRequest;
import com.dam.coach.rest.message.coachAction.DropRequest;
import com.dam.coach.textPreparation.TextReplacer;
import com.dam.coach.textPreparation.TextReplacerImpl;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class CoachActionStore {

	@Autowired
	private CoachActionModel coachActionModel;

	public long count() {
		return coachActionModel.count();
	}

	public CoachAction getRawActionSafe(CoachActionRequest request) throws DamServiceException {
		PermissionCheck.checkRequestedParams(request, request.getRequestorUserId(),
				request.getRights());

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(request.getRequestorUserId(), null,
				request.getRights());
		
		return getRawActionByReference(request.getCoachAction().getActionReference());
	}
	
	public CoachAction getActionSafe(CoachActionRequest request) throws DamServiceException {
		PermissionCheck.checkRequestedParams(request, request.getRequestorUserId(),
				request.getRights());

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(request.getRequestorUserId(), null,
				request.getRights());
		
		return getActionByReference(request.getCoachAction().getActionReference());
	}
	
	public List<CoachAction> getActionListSafe(CoachActionRequest request) throws DamServiceException {
		PermissionCheck.checkRequestedParams(request, request.getRequestorUserId(),
				request.getRights());

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(request.getRequestorUserId(), null,
				request.getRights());
		
		return getActionList();
	}
	
	public CoachAction getRawActionByReference(String actionReference) {
		return coachActionModel.findByActionReference(actionReference);
	}
	
	
	public CoachAction getActionByReference(String actionReference) {
		CoachAction action = coachActionModel.findByActionReference(actionReference);
		if (null != action) {
			action.setMessage(replaceMessage(action.getMessage()));
		}
		return action;
	}
	
	public CoachAction saveDepot(CoachAction coachAction) throws DamServiceException {
		return coachActionModel.save(coachAction);
	}
	
	public List<CoachAction>getActionList() {
		List<CoachAction> actions = new ArrayList<>();		
		coachActionModel.findAll().iterator().forEachRemaining(actions::add);		
		Iterator<CoachAction> it = actions.iterator();
		while (it.hasNext()) {
			CoachAction action = it.next();
			action.setMessage(replaceMessage(action.getMessage()));
		}

		return actions;
	}


	public CoachAction createCoachActionSafe(CoachActionCreateRequest coachActionCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(coachActionCreateRequest, coachActionCreateRequest.getRequestorUserId(),
				coachActionCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(coachActionCreateRequest.getCoachAction(), CoachAction.class, "CoachAction Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(coachActionCreateRequest.getRequestorUserId(), null,
				coachActionCreateRequest.getRights());

		// check if still exists
		// direct by portfolioId
		CoachAction existingCoachAction = getCoachActionById(coachActionCreateRequest.getCoachAction().getActionId());

		if (null == existingCoachAction) {
			existingCoachAction = getCoachActionByReference(coachActionCreateRequest.getCoachAction().getActionReference());
		}

		if (null != existingCoachAction) {
			return updateCoachAction(existingCoachAction, coachActionCreateRequest.getCoachAction());
		}

		CoachAction coachAction;

		try {
			// save if all checks are ok and the portfolio doesn't exist
			coachAction = coachActionModel.save(coachActionCreateRequest.getCoachAction());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "CoachActionconstruction could not be stored", e.getMessage());
		}

		if (null == coachAction) {
			throw new DamServiceException(new Long(422), "CoachAction not created",
					"CoachAction still exists, data invalid or not complete");
		}
		return coachAction;

	}
	
	public Long dropActionSafe(DropRequest dropRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(dropRequest, dropRequest.getRequestorUserId(),
				dropRequest.getRights());
		PermissionCheck.checkRequestedEntity(dropRequest.getActionReference(), String.class, "String Class");

		// Save database requests
		PermissionCheck.isDeletePermissionSet(dropRequest.getRequestorUserId(), null,
				dropRequest.getRights());

		CoachAction existingAction = getRawActionByReference(dropRequest.getActionReference());

		if (null == existingAction) {
			throw new DamServiceException(new Long(404), "CoachAction could not be dropped",
					"CoachAction does not exist or could not be found in database.");
		}
		dropCoachAction(existingAction);

		return 200L;
	}
	
	public CoachAction getCoachActionById(Long actionId) {
		if (null == actionId) {
			return null;
		}

		Optional<CoachAction> optionalCoachAction = coachActionModel.findById(actionId);
		if (null != optionalCoachAction && optionalCoachAction.isPresent()) {
			return optionalCoachAction.get();
		}
		return null;
	}
	
	private CoachAction updateCoachAction(CoachAction coachActionForUpdate, CoachAction coachActionContainer)
			throws DamServiceException {

		if (null != coachActionForUpdate && null != coachActionContainer) {
			coachActionForUpdate.updateEntity(coachActionContainer);
			try {
				return coachActionModel.save(coachActionForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "CoachAction could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "CoachAction could not be saved",
				"Check CoachAction data in request.");
	}


	private CoachAction getCoachActionByReference(String actionReference) {
		return coachActionModel.findByActionReference(actionReference);
	}


	private Long dropCoachAction(CoachAction coachAction) {
		if (null != coachAction) {
			coachActionModel.deleteById(coachAction.getActionId());
			CoachAction deletedCoachAction = getCoachActionById(coachAction.getActionId());
			if (null == deletedCoachAction) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

	
	private String replaceMessage(String source) {
		TextReplacer replacer = new TextReplacerImpl();
		return replacer.replace(source);
	}

}
