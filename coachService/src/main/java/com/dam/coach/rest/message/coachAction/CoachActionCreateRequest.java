package com.dam.coach.rest.message.coachAction;

import com.dam.coach.model.entity.CoachAction;

public class CoachActionCreateRequest extends CoachActionWriteRequest {

    public CoachActionCreateRequest(Long requestorUserId, CoachAction coachAction) {
		super(requestorUserId, coachAction);
    }
    
}