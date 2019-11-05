package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.RestRequest;

public class CoachActionRequest extends RestRequest {
	
	private CoachAction coachAction;
	private Long userId;
	
    public CoachActionRequest( Long requestorUserId, Long userId, CoachAction coachAction) {
		super("DAM 2.0");
		setCoachAction(coachAction);
		setRequestorUserId(requestorUserId);
		setUserId(userId);
	}

	public CoachAction getCoachAction() {
		return coachAction;
	}

	public void setCoachAction(CoachAction coachAction) {
		this.coachAction = coachAction;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
