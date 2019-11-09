package com.dam.coach.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.RestResponse;
import com.dam.coach.rest.message.coachAction.CoachActionCreateRequest;
import com.dam.coach.rest.message.coachAction.CoachActionCreateResponse;
import com.dam.coach.rest.message.coachAction.CoachActionListResponse;
import com.dam.coach.rest.message.coachAction.CoachActionRequest;
import com.dam.coach.rest.message.coachAction.CoachActionResponse;
import com.dam.coach.rest.message.coachAction.DropRequest;
import com.dam.coach.rest.message.coachAction.DropResponse;
import com.dam.coach.store.CoachActionStore;
import com.dam.coach.textPreparation.TextReplacer;
import com.dam.coach.textPreparation.TextReplacerImpl;
import com.dam.exception.DamServiceException;

@CrossOrigin
@RestController
public class CoachActionController {
	@Autowired
	private CoachActionStore coachActionStore;
	
	@PostMapping("/dropAction")
	public RestResponse dropCoachAction(@RequestBody DropRequest dropRequest) throws DamServiceException {
		try {
			return new DropResponse(coachActionStore.dropActionSafe(dropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createAction")
	public RestResponse createCoachAction(@RequestBody CoachActionCreateRequest coachActionCreateRequest)
			throws DamServiceException {
		try {
			return new CoachActionCreateResponse(coachActionStore.createCoachActionSafe(coachActionCreateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getAction")
	public RestResponse getCoachAction(@RequestBody CoachActionRequest coachActionRequest) throws DamServiceException {
		try {
			return new CoachActionResponse(coachActionStore.getActionSafe(coachActionRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getActionList")
	public RestResponse getCoachActionList(@RequestBody CoachActionRequest coachActionRequest)
			throws DamServiceException {
		try {
			return new CoachActionListResponse(coachActionStore.getActionListSafe(coachActionRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getActionReplaced")

	public RestResponse getActionReplaced(@RequestBody CoachActionRequest coachActionRequest)
			throws DamServiceException {
		try {
			CoachAction coachAction = coachActionStore.getActionSafe(coachActionRequest);
			TextReplacer replacer = new TextReplacerImpl();
			coachAction.setMessage(replacer.replace(coachAction.getMessage()));

			return new CoachActionResponse(coachAction);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}

	}
}