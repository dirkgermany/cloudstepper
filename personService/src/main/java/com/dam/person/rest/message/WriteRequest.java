package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public abstract class WriteRequest extends RestRequest {

	private Person person = new Person();	

    public WriteRequest(Long requestorUserId, Person person) {
		super("CS 0.0.1");
		setPerson(person);
		setRequestorUserId(requestorUserId);
    }
        
    public Person getPerson() {
    	return person;
    }

	public void setPerson(Person person) {
		this.person = person;
	}
    
}