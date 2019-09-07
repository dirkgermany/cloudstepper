package com.dam.userService.rest.message;

import com.dam.userService.model.entity.User;

public class UpdateRequest extends RestRequest {

	private User userStored = new User();
	private User userUpdate = new User();

    public UpdateRequest(User userStored, User userUpdate) {
		super("DAM 2.0");
		this.userStored = userStored;
		this.userUpdate = userUpdate;
    }
    
//    public UpdateRequest(String userName, String password, String givenName, String lastName) {
//		super("DAM 2.0");
//		user = new User(userName, password, givenName, lastName);
//    }
    
    public User getUserStored() {
    	return userStored;
    }
    
    public User getUserUpdate() {
    	return userUpdate;
    }
    
}