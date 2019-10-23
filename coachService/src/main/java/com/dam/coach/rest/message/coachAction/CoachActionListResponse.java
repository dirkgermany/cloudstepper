package com.dam.coach.rest.message.coachAction;

import java.util.List;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.RestResponse;

public class CoachActionListResponse extends RestResponse{

	private List<CoachAction> coachActionList;
    	
	public CoachActionListResponse (List<CoachAction> coachActionList) {
		super(new Long(200), "OK", "Coach Request Successfull");
		setCoachActionList(coachActionList);
	}

	public List<CoachAction> getCoachActionList() {
		return coachActionList;
	}

	public void setCoachActionList(List<CoachAction> coachActionList) {
		this.coachActionList = coachActionList;
	}  
}
