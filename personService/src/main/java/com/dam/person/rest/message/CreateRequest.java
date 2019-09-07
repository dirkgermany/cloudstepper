package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class CreateRequest extends WriteRequest {

    public CreateRequest(Long requestorUserId, Person person) {
		super(requestorUserId, person);
    }
    
}