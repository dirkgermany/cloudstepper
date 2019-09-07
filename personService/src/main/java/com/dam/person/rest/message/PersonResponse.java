package com.dam.person.rest.message;

import com.dam.person.model.entity.Person;

public class PersonResponse extends RestResponse{
    private Person user;
    	
	public PersonResponse (Person user) {
		super(new Long(200), "OK", "Person found");
		
		setUser(user);
	}  

	private void setUser(Person user) {
		this.user = user;
	}
	
	public Person getUser() {
		return this.user;
	}
	
}
