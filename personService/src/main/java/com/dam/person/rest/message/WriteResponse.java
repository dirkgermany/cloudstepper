package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public abstract class WriteResponse extends RestResponse{
    private Person person;
	
	public WriteResponse(Long result, String shortStatus, String longStatus, Person person) {
		super(result, shortStatus, longStatus);
		setPerson(person);
	}

	private void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return person;
	}
	
}