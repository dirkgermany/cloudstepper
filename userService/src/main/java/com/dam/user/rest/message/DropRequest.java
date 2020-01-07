package com.dam.user.rest.message;

import com.dam.user.model.entity.User;

public class DropRequest extends RestRequest {

	private User user = new User();	

    public DropRequest(User user) {
		super("CS 0.0.1");
		this.user = user;
    }
    
    public DropRequest(String userName, String password, String givenName, String lastName, String role) {
		super("CS 0.0.1");
		user = new User(userName, password, givenName, lastName, role);
    }
    
    public User getUser() {
    	return user;
    }
    
}