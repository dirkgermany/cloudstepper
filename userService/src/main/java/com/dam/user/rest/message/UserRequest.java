package com.dam.user.rest.message;

import com.dam.user.model.entity.User;

public class UserRequest extends RestRequest {
    private User user = new User();

    public UserRequest(String userName, String password, String requestorUserId) {
		super("CS 0.0.1");
		user.setUserName(userName);
		user.setPassword(password);
		setRequestorUserId(requestorUserId);
	}
    
    public User getUser() {
    	return user;
    }
    
}