package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;

public abstract class CoachActionWriteResponse extends com.dam.coach.rest.message.RestResponse{
    private CoachAction coachAction;
	
	public CoachActionWriteResponse(Long result, String shortStatus, String longStatus, CoachAction coachAction) {
		super(result, shortStatus, longStatus);
		setCoachAction(coachAction);
	}

	private void setCoachAction(CoachAction coachAction) {
		this.coachAction = coachAction;
	}
	
	public CoachAction getCoachAction() {
		return coachAction;
	}
}
