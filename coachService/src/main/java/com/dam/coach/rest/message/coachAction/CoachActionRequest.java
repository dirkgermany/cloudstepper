package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.RestRequest;

public class CoachActionRequest extends RestRequest {
	
	private CoachAction coachAction;
	
    public CoachActionRequest( Long requestorUserId, CoachAction coachAction) {
		super("DAM 2.0");
		setCoachAction(coachAction);
		setRequestorUserId(requestorUserId);
	}

	public CoachAction getCoachAction() {
		return coachAction;
	}

	public void setCoachAction(CoachAction coachAction) {
		this.coachAction = coachAction;
	}
}
