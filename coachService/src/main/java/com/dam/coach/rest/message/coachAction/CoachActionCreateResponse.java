package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;

public class CoachActionCreateResponse extends CoachActionWriteResponse{

	public CoachActionCreateResponse(CoachAction coachAction) {
		super(new Long(200), "OK", "Coach Action Saved", coachAction);
	}
    	
}
