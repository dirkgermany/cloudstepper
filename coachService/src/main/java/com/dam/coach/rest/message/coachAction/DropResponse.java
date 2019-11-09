package com.dam.coach.rest.message.coachAction;

import com.dam.coach.rest.message.RestResponse;

public class DropResponse extends RestResponse{
    	
	public DropResponse (Long result) {
		super(new Long(200), "OK", "Coach Action(s) dropped.");
	}  
}
