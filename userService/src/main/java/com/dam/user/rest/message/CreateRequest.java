package com.dam.user.rest.message;

import com.dam.user.model.entity.User;
import com.dam.user.types.String;

public class CreateRequest extends RestRequest {

	private User user = new User();	

    public CreateRequest(User user) {
		super("CS 0.0.1");
		this.user = user;
    }
    
    public CreateRequest(String userName, String password, String givenName, String lastName, String role) {
		super("CS 0.0.1");
		user = new User(userName, password, givenName, lastName, role);
    }
    
    public User getUser() {
    	return user;
    }
    
}