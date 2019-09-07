package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class DropRequest extends WriteRequest {

	public DropRequest(Long requestorUserId, Person person) {
		super(requestorUserId, person);
    }
}