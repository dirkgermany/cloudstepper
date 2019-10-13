package com.dam.coach.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.coach.rest.message.RestResponse;
import com.dam.coach.rest.message.coachAction.CoachActionRequest;
import com.dam.coach.rest.message.coachAction.CoachActionResponse;
import com.dam.coach.store.CoachActionStore;
import com.dam.exception.DamServiceException;

@CrossOrigin
@RestController
public class CoachActionController {
	@Autowired
	private CoachActionStore coachActionStore;

	@PostMapping("/getAction")
	public RestResponse getCoachAction(@RequestBody CoachActionRequest coachActionRequest) throws DamServiceException {
		try {
			return new CoachActionResponse(coachActionStore.getActionSafe(coachActionRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}