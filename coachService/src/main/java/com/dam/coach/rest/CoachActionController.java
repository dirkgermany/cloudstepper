package com.dam.coach.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
public class CoachActionController extends CoachController{
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
	public RestResponse postActionReplaced(@RequestBody CoachActionRequest coachActionRequest)
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

	@GetMapping("/getActionReplaced")
	public RestResponse getActionReplaced(@RequestParam Map<String, String> requestParams) throws DamServiceException {

		try {
			String requestorUserIdString = decode(requestParams.get("requestorUserId"));
			String userIdString = decode(requestParams.get("userId"));
			String rights = decode(requestParams.get("rights"));
			Long userId = null;

			if (null != userIdString && !userIdString.isEmpty()) {
				userId = Long.valueOf(userIdString);
			}
			Long requestorUserId = Long.valueOf(requestorUserIdString);
			String actionReference = decode(requestParams.get("actionReference"));

			CoachAction createdCoachAction = new CoachAction();
			createdCoachAction.setActionReference(actionReference);
			CoachActionRequest createdRequest = new CoachActionRequest(requestorUserId, userId, createdCoachAction);
			createdRequest.setRights(rights);

			CoachAction coachAction = coachActionStore.getActionSafe(createdRequest);
			
			return new CoachActionResponse(coachAction);

		} catch (Exception e) {
			return new RestResponse(404L, e.getMessage(), e.getStackTrace().toString());
		}
	}
}