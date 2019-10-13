package com.dam.coach.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.coach.PermissionCheck;
import com.dam.coach.model.CoachActionModel;
import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.coachAction.CoachActionRequest;
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

	public CoachAction getActionSafe(CoachActionRequest request) throws DamServiceException {
		PermissionCheck.checkRequestedParams(request, request.getRequestorUserId(),
				request.getRights());

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(request.getRequestorUserId(), null,
				request.getRights());
		
		return getActionByReference(request.getCoachAction().getActionReference());
	}
	
	public CoachAction getActionByReference(String actionReference) {
		return coachActionModel.findByActionReference(actionReference);
	}
	
	public CoachAction saveDepot(CoachAction coachAction) throws DamServiceException {
		return coachActionModel.save(coachAction);
	}

}
