package com.dam.user.rest.message;

import com.dam.user.model.entity.User;

public class UpdateRequest extends RestRequest {

	private User userStored = new User();
	private User userUpdate = new User();

    public UpdateRequest(User userStored, User userUpdate) {
		super("CS 0.0.1");
		this.userStored = userStored;
		this.userUpdate = userUpdate;
    }
    
//    public UpdateRequest(String userName, String password, String givenName, String lastName) {
//		super("CS 0.0.1");
//		user = new User(userName, password, givenName, lastName);
//    }
    
    public User getUserStored() {
    	return userStored;
    }
    
    public User getUserUpdate() {
    	return userUpdate;
    }
    
}