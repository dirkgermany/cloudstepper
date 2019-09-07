package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public class UpdateRequest extends WriteRequest {

	private Long userId;
	private Long personId;

    public UpdateRequest(Long requestorUserId, Long personId, Long userId, Address person) {
    	super (requestorUserId, person);
    	setUserId(userId);
    	setPersonId(personId);
    }
    
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}
    
}