package com.dam.coach.rest.message.coachAction;

import com.dam.coach.rest.message.RestRequest;

public class DropRequest extends RestRequest {

	private String actionReference;


    public DropRequest(Long requestorUserId, String actionReference) {
		super("DAM 2.0");
		setActionReference(actionReference);
		setRequestorUserId(requestorUserId);
    }

	public String getActionReference() {
		return actionReference;
	}

	public void setActionReference(String actionReference) {
		this.actionReference = actionReference;
	}
}