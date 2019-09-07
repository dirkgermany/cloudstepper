package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class UpdateRequest extends WriteRequest {

    public UpdateRequest(Long requestorUserId, Person person) {
    	super (requestorUserId, person);
    }
}