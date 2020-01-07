package com.dam.person.rest.message;

public class PersonRequest extends RestRequest {
    private Long userId;
    private Long personId;

    public PersonRequest( Long requestorUserId, Long personId, Long userId) {
		super("CS 0.0.1");
		setUserId(userId);
		setPersonId(personId);
		setRequestorUserId(requestorUserId);
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