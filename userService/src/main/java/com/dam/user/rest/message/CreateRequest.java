package com.dam.user.rest.message;

import com.dam.user.model.entity.User;
import com.dam.user.types.Role;

public class CreateRequest extends RestRequest {

	private User user = new User();	

    public CreateRequest(User user) {
		super("DAM 2.0");
		this.user = user;
    }
    
    public CreateRequest(String userName, String password, String givenName, String lastName, Role role) {
		super("DAM 2.0");
		user = new User(userName, password, givenName, lastName, role);
    }
    
    public User getUser() {
    	return user;
    }
    
}