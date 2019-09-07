package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class UpdateResponse extends WriteResponse{
    	
	public UpdateResponse (Person person) {
		super(new Long(200), "OK", "Person Updated", person);
	}  

}
