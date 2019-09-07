package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class CreateResponse extends WriteResponse{

	public CreateResponse(Person person) {
		super(new Long(200), "OK", "Person Saved", person);
	}
    	
}
