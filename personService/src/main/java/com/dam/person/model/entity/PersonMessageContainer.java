package com.dam.person.model.entity;

import org.springframework.stereotype.Component;

@Component
public class PersonMessageContainer {
	
	private Long userId;
	private String givenName;
	private String lastName;
	
	public PersonMessageContainer () {
		
	}
	
	public PersonMessageContainer (Long userId, String givenName, String lastName) {
		setUserId(userId);
		setGivenName(givenName);
		setLastName(lastName);
	}
	
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
