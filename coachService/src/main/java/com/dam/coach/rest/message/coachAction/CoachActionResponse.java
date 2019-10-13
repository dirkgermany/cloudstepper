package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.RestResponse;

public class CoachActionResponse extends RestResponse{

	private CoachAction coachAction;
    	
	public CoachActionResponse (CoachAction coachAction) {
		super(new Long(200), "OK", "Coach Request Successfull");
		setCoachAction(coachAction);
	}

	public CoachAction getCoachAction() {
		return coachAction;
	}

	public void setCoachAction(CoachAction coachAction) {
		this.coachAction = coachAction;
	}  
}
